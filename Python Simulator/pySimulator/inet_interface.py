from pySimulator.simulators import * 
from pySimulator.nodes import *
from pySimulator.connections import *
from pySimulator.detectors import *
from pySimulator.networks import *

def load(inet_name):

	inet = {}
	with open(inet_name, "r") as inet_file:
		code = inet_file.read()
	exec(code)

	try:
		return inet['network'], inet['simulator']
	except Exception:
		print("inet['network'] and inet['simulator'] must be defined in the inet file")
		return None, None


def save(network, simulator, file_name):
	inet_file = open(file_name, "w")
	setup_instructions = 	(
								"inet['network'] = Network() \n"
								"inet['simulator'] = Simulator(inet['network']) \n"
								"\n"
								"net = inet['network'] \n"
								"sim = inet['simulator'] \n"
							)
	inet_file.write(setup_instructions)
	inet_file.write(network.to_inet_string()+'\n')
	inet_file.write(simulator.to_inet_string())
	inet_file.close()