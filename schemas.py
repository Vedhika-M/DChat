from pydantic import BaseModel
from datetime import datetime

class CreateUser(BaseModel):
    username: str
    email: str
    password: str

class UpdateUser(BaseModel):
    username: str
    email: str
    password: str

class LoginUser(BaseModel):
    email: str
    password: str

class UserResponse(BaseModel):
    id: int
    username: str
    class Config:
        from_attributes = True

class MessageResponse(BaseModel):
    id: int
    sender_id: int
    receiver_id: int
    content: str
    timestamp: datetime
    class Config:
        from_attributes = True

class CreateMessage(BaseModel):
    receiver_id: int
    content: str