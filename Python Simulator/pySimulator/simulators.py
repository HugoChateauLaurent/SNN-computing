import numpy as np
from .detectors import *

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
			

	def createMultimeter(self, targets=[], ID=None, increment_count=True):
		detector = Multimeter(targets, ID, increment_count)
		self.detectors.append(detector)
		return detector

	def createRaster(self, targets=[], ID=None, increment_count=True):
		detector = Raster(targets, ID, increment_count)
		self.detectors.append(detector)
		return detector
		

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
			detector.initialize(steps)

		for i in range(steps):
			self.network.step()

			for detector in self.detectors:
				detector.step()

	def to_inet_string(self):
		inet_str = ''

		for d in self.detectors:
			inet_str += d.to_inet_string() + '\n\n'

		return inet_str


