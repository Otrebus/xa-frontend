.section data
MyLedDriver:
  dword 2
MyLedDriver_blinking:
  byte
Main:
  dword

.section code
MyLedDriver_handleMsg:
  push word [$fp+4] -- length
  push word 1
  jge word noret -- if length > 0, branch
  ret 4 -- return to caller: restore fp, pop args (2*2 bytes)

noret:
  push [$fp+2] -- pointer to msg
  push byte -- get value pointed to by msg, that is, msg[0]
  push byte 49 -- '1'
  jne word noblink

  push byte 1 -- true
  push MyLedDriver_blinking -- address of blinking
  push byte -- blinking = true
  jmp afterelse

noblink:
  call stopBlinking
afterelse:
  ret 4

MyLedDriver_blink:
  push byte 1 -- prepare to compare “blinking” variable
  push [MyLedDriver_blinking] -- same
  jne word nostartblink -- if blinking != 1, we stop by returning
  push MyLedDriver_blink
  push MyLedDriver
  push dword 200000
  push dword 5000
  pushb 0
  async
  
nostartblink:
  ret 0

MyLedDriver_isBlinking:
  push byte [MyLedDriver_blinking]
  push $fp+3 -- address of the part of the stack that the callee reserved
               -- for the return value
  pop byte -- load the value of blinking to the position given above
  ret 1
  
  
Main_main:
  push MyLedDriver_handleMsg
  push 
  call
