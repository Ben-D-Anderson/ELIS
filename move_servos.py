import RPi.GPIO as GPIO
from time import sleep
import sys

GPIO.setmode(GPIO.BOARD)
GPIO.setup(3, GPIO.OUT)
GPIO.setup(5, GPIO.OUT)
pwm1=GPIO.PWM(3, 50)
pwm2=GPIO.PWM(5, 50)
pwm1.start(0)
pwm2.start(0)
def SetAngle1(angle):
    duty = (angle/27) + 2
    GPIO.output(3, True)
    #GPIO.output(5, True)
    print(duty)
    pwm1.ChangeDutyCycle(duty)
    sleep(2)
    GPIO.output(3, False)
   # GPIO.output(5, False)
    pwm1.ChangeDutyCycle(0)
def SetAngle2(angle):
    duty = (angle/27) + 2
    duty2 = ((180-angle)/27)+2
    GPIO.output(5, True)
    pwm2.ChangeDutyCycle(duty2)
    GPIO.output(5, False)
    GPIO.output(3, True)
    pwm1.ChangeDutyCycle(duty)
    GPIO.output(3, False)
    sleep(2)
    pwm1.ChangeDutyCycle(2)
    pwm2.ChangeDutyCycle(12)

angle_to_turn = int(sys.argv[0])

#directly up should be set to zero

SetAngle1(90 + angle_to_turn)
SetAngle2(90 - angle_to_turn)

pwm1.stop()
pwm2.stop()
GPIO.cleanup()
