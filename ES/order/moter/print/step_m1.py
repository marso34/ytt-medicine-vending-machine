import RPi.GPIO as GPIO
from time import sleep
import threading

# GPIO 설정
GPIO.setmode(GPIO.BCM)

# ULN2003 제어 핀 정의 (라즈베리파이에 연결되는 핀)
IN1 = 17   # 모터 IN1
IN2 = 4   # 모터 IN2
IN3 = 3  # 모터 IN3
IN4 = 2  # 모터 IN4

# GPIO 핀을 출력 모드로 설정
GPIO.setup(IN1, GPIO.OUT)
GPIO.setup(IN2, GPIO.OUT)
GPIO.setup(IN3, GPIO.OUT)
GPIO.setup(IN4, GPIO.OUT)

# 스텝 순서 정의 (한 스텝씩 모터를 회전시키는 시퀀스)
step_sequence = [
    [1, 0, 1, 0],
    [0, 1, 1, 0],
    [0, 1, 0, 1],
    [1, 0, 0, 1]
]

# 한 바퀴에 필요한 스텝 수 (28BYJ-48의 경우 보통 2048 스텝)
steps_per_revolution = 2048

# 모터를 한 바퀴 회전시키는 함수 (정회전)
def rotate_motor_one_turn():
    for step in range(steps_per_revolution):
        for pin in range(4):
            GPIO.output([IN1, IN2, IN3, IN4][pin], step_sequence[step % 4][pin])
        sleep(0.01)  # 스텝 사이의 지연 시간으로 속도 조절

try:
    rotate_motor_one_turn()  # 모터를 한 바퀴 정회전
    sleep(1)  # 1초 대기

finally:
    GPIO.cleanup()  # GPIO 핀 초기화
