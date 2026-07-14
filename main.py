from fastapi import FastAPI, HTTPException, Depends, WebSocket, WebSocketDisconnect, Query
from database import engine,SessionLocal
from models import User,Base,Message
from schemas import CreateUser,UpdateUser,UserResponse,LoginUser,MessageResponse,CreateMessage
from auth import hash_password,verify_password
from jwt_handler import create_access_token,verify_access_token
from fastapi.security import OAuth2PasswordBearer,OAuth2PasswordRequestForm
from sqlalchemy import or_, and_
from connection_manager import manager

app = FastAPI()
oauth2_scheme = OAuth2PasswordBearer(tokenUrl="login")

Base.metadata.create_all(bind=engine)


@app.post("/signup")
def signup(user: CreateUser):
    db = SessionLocal()

    existing_username = db.query(User).filter(User.username == user.username).first()
    if existing_username:
        db.close()
        raise HTTPException(status_code=400, detail="Username already exists")

    existing_email = db.query(User).filter(User.email == user.email).first()
    if existing_email:
        db.close()
        raise HTTPException(status_code=400, detail="Email already exists")

    new_user = User(
        username=user.username,
        email=user.email,
        password=hash_password(user.password)
    )

    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    db.close()

    return {
        "message": "User created successfully",
        "user_id": new_user.id
    }


@app.get("/users",response_model=list[UserResponse])
def get_users():
    db = SessionLocal()

    users = db.query(User).all()
   
    db.close()
    
    return users


@app.get("/users/{id}")
def get_user(id: int):
    db = SessionLocal()

    user = db.query(User).filter(User.id == id).first()

    if user is None:
        db.close()
        raise HTTPException(status_code=404, detail="User not found")

    db.close()

    return user


@app.put("/users/{id}")
def update_user(id: int, updated_user: UpdateUser):
    db = SessionLocal()

    user = db.query(User).filter(User.id == id).first()

    if user is None:
        db.close()
        raise HTTPException(status_code=404, detail="User not found")

    user.username = updated_user.username
    user.email = updated_user.email
    user.password = hash_password(updated_user.password)

    db.commit()
    db.refresh(user)
    db.close()

    return {
        "message": "User updated",
        "user": user
    }


@app.delete("/users/{id}")
def delete_user(id: int):
    db = SessionLocal()

    user = db.query(User).filter(User.id == id).first()

    if user is None:
        db.close()
        raise HTTPException(status_code=404, detail="User not found")

    db.delete(user)
    db.commit()
    db.close()

    return {
        "message": "User deleted successfully"
    }


@app.post("/login")
def login(form_data: OAuth2PasswordRequestForm = Depends()):

    db = SessionLocal()

    existing_user = db.query(User).filter(
        User.username == form_data.username
    ).first()

    if existing_user is None:
        db.close()
        raise HTTPException(
            status_code=404,
            detail="User not found"
        )

    if not verify_password(form_data.password, existing_user.password):
        db.close()
        raise HTTPException(
            status_code=401,
            detail="Invalid password"
        )

    access_token = create_access_token(
        data={
            "user_id": existing_user.id,
            "username": existing_user.username
        }
    )
    db.close()

    return {
        "access_token": access_token,
        "token_type": "bearer"
    }

def get_current_user(token: str = Depends(oauth2_scheme)):
    payload = verify_access_token(token)

    if payload is None:
        raise HTTPException(status_code=401, detail="Invalid token")

    user_id = payload.get("user_id")

    db = SessionLocal()

    user = db.query(User).filter(User.id == user_id).first()

    db.close()

    if user is None:
        raise HTTPException(status_code=401, detail="User not found")

    return user


@app.get("/profile")
def profile(user: User = Depends(get_current_user)):
    return {
        "id": user.id,
        "username": user.username,
        "email": user.email
    }


@app.post("/messages/send", response_model=MessageResponse)
def send_message(
    message: CreateMessage,
    current_user: User = Depends(get_current_user)
):
    db = SessionLocal()

    receiver = db.query(User).filter(
        User.id == message.receiver_id
    ).first()

    if receiver is None:
        db.close()
        raise HTTPException(
            status_code=404,
            detail="Receiver not found"
        )

    new_message = Message(
        sender_id=current_user.id,
        receiver_id=message.receiver_id,
        content=message.content
    )

    db.add(new_message)
    db.commit()
    db.refresh(new_message)
    db.close()

    return new_message


@app.get("/messages/{user_id}", response_model=list[MessageResponse])
def get_messages(
    user_id: int,
    current_user: User = Depends(get_current_user)
):
    db = SessionLocal()

    user = db.query(User).filter(User.id == user_id).first()

    if user is None:
        db.close()
        raise HTTPException(
            status_code=404,
            detail="User not found"
        )

    messages = db.query(Message).filter(
        or_(
            and_(
                Message.sender_id == current_user.id,
                Message.receiver_id == user_id
            ),
            and_(
                Message.sender_id == user_id,
                Message.receiver_id == current_user.id
            )
        )
    ).order_by(Message.timestamp).all()

    db.close()

    return messages


@app.websocket("/ws")
async def websocket_endpoint(
    websocket: WebSocket,
    token: str = Query(...)
):
    payload = verify_access_token(token)

    if payload is None:
        await websocket.close(code=1008)
        return

    user_id = payload.get("user_id")

    if user_id is None:
        await websocket.close(code=1008)
        return
    
    await manager.connect(user_id, websocket)

    try:
        while True:
            data = await websocket.receive_json()

            db = SessionLocal()

            new_message = Message(
                sender_id=user_id,
                receiver_id=data["receiver_id"],
                content=data["content"]
            )

            db.add(new_message)
            db.commit()
            db.refresh(new_message)

            message_data = {
                "id": new_message.id,
                "sender_id": new_message.sender_id,
                "receiver_id": new_message.receiver_id,
                "content": new_message.content,
                "timestamp": str(new_message.timestamp)
           }

            await manager.send_personal_message(
                data["receiver_id"],
                message_data
           )

            await manager.send_personal_message(
                user_id,
                message_data
            )
            db.close()

    except WebSocketDisconnect:
        manager.disconnect(user_id)