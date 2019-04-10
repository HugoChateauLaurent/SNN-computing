import numpy as np

class Simulator():
	"""Simulator

    Parameters
    ----------
    network : Network
        Network to simulate
    detectors : List
    	List of detectors
	"""

	def __init__(self, network, detectors=[], seed=None):
		self.network = network
		self.detectors = detectors
		if seed != None:
			self.network.update_rng(np.random.RandomState(seed))

	def run(self, steps):
		"""Run the simulator

	    Parameters
	    ----------
	    steps : int
	        Number of steps to simulate
		"""
		
		# uncomment if implementing dt (but the project seems stepwise oriented)
		# steps = int(np.round(float(seconds) / self.dt))

		for detector in self.detectors:
			detector.init(steps)

		for i in range(steps):
			self.network.step()

			for detector in self.detectors:
				detector.step()


