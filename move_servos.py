import RPi.GPIO as GPIO
from time import sleep
import sys

first_pin = 32
second_pin = 33

GPIO.setmode(GPIO.BOARD)
GPIO.setup(first_pin, GPIO.OUT)
GPIO.setup(second_pin, GPIO.OUT)
pwm1=GPIO.PWM(first_pin, 50)
pwm2=GPIO.PWM(second_pin, 50)
pwm1.start(0)
pwm2.start(0)

def SetAngle(angle, angle2):
    duty = (angle/27) + 2
    duty2 = ((180-angle2)/27)+2
    GPIO.output(second_pin, True)
    pwm2.ChangeDutyCycle(duty2)
    GPIO.output(second_pin, False)
    GPIO.output(first_pin, True)
    pwm1.ChangeDutyCycle(duty)
    GPIO.output(first_pin, False)
    sleep(1)
    pwm1.ChangeDutyCycle(2)
    pwm2.ChangeDutyCycle(12)

angle_to_turn = int(sys.argv[1])

#directly up should be set to zero
#but when angle_to_turn=0, servos should be horizontal so platform is flat.

#SetAngle(90 + angle_to_turn, 90 - angle_to_turn)
SetAngle(90 + angle_to_turn, 90 + angle_to_turn)

pwm1.stop()
pwm2.stop()
GPIO.cleanup()
