import sys
import uuid

for line in sys.stdin:
    u = uuid.uuid4()
    print(line.rstrip('\r\n')+'\t'+str(u))
