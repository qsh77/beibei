#!/usr/bin/env python3
import bcrypt

password = "admin123"
salt = bcrypt.gensalt()
hashed = bcrypt.hashpw(password.encode('utf-8'), salt)
print(f"Password: {password}")
print(f"BCrypt Hash: {hashed.decode('utf-8')}")

# 验证
is_valid = bcrypt.checkpw(password.encode('utf-8'), hashed)
print(f"Verification: {is_valid}")