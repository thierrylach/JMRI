print ("JythonPart2Test")
# see if same context using settings from 1st script
global jyPersistanceCheck
if (jyPersistanceCheck != 81) : raise AssertionError('Variable not persistant')
