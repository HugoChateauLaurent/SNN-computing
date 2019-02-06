import numpy as np

class Simulator():
	"""Simulator

    Parameters
    ----------
    network : Network
        Network to simulate
	"""

	def __init__(self, network, detectors=[]):
		self.network = network
		self.detectors = detectors

	def run(self, steps):
		"""Run the simulator

	    Parameters
	    ----------
	    steps : int
	        Number of steps to simulate
        multimeter : boolean (Default: True)
        	Whether or not to record the membrane voltages of each neuron
		"""
		
		# uncomment if implementing dt (but the project seems stepwise oriented)
		# steps = int(np.round(float(seconds) / self.dt))

		for detector in self.detectors:
			detector.init(steps)

		for i in range(steps):
			self.network.step()

			for detector in self.detectors:
				detector.step()


