.entry
Main:
byte[6]
dword -- main "object"
CounterObject:
dword
CounterObject_count:
dword 100 -- count
.program
.entry
Main_main:
push dword 1000
push CounterObject_startCounting
push CounterObject
sync

CounterObject_startCounting:
push dword [$fp-6]
pop dword [CounterObject_count]
push CounterObject_inc
push CounterObject
push dword 10000
push dword 1000
push byte 0
async
ret 4

CounterObject_inc:
push CounterObject_count
push dword 1
add dword
push CounterObject_inc
push CounterObject
push dword 10000
push dword 1000
push byte 0
async
ret 0

push dword 2
push dword 3
sub dword
jgz dword toggleLed
jez word toggleLed
jnez byte toggleLed
jgez dword toggleLed
pop byte
pop word
pop dword
pop 5

.extern
toggleLed:
  "toggleLed"
setLed:
  "setLed"