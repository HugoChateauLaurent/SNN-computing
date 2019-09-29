from pySimulator.simulators import * 
from pySimulator.nodes import *
from pySimulator.connections import *
from pySimulator.detectors import *
from pySimulator.networks import *

def load(inet_name):

	inet = {}
	network = Network([], [])
	simulator = Simulator(network, [])
	file = open(inet_name, "r")
	exec(file.read())

	return inet['network'], inet['simulator']

