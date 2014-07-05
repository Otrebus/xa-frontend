  push 3
  push dword [$fp+3]
  push blah
noret:
  push word 0xa2
  push word -0xa2
  push noret
blah:
  push dword [$fp + 3]
  push dword -2
  push byte [noret]
  push noret