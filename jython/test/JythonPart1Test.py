print ("JythonPart1Test")

import jmri as jmri
import java

# access constants

if (jmri.Turnout.THROWN != 4) : raise AssertionError('Constant THROWN not right')

# access InstanceManager to get manager
sm = jmri.InstanceManager.getNullableDefault(jmri.SensorManager)

if (sm == None) : raise AssertionError('No instance manager access')

# check against simpler syntax
if (sm != sensors) : raise AssertionError('Not same SensorManager')

# use that manager to affect JMRI
IS1 = sm.provideSensor("IS1")

if (sm.getSensor("IS1") == None) : raise AssertionError('Sensor not created')

# extending a class
from jmri.jmrit.automat import AbstractAutomaton

class Automat(AbstractAutomaton) :
    def init(self) :
        print ("init in Python 3")
    def handle(self) :
        print ("handle in Python 3")
        return False
Automat().start()

# prep to check for persistent context
global jyPersistanceCheck
jyPersistanceCheck = 81
