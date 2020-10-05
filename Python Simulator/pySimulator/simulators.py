import numpy as np
from .detectors import *
import matplotlib.ticker as ticker

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

		self.print_detectors(steps)

	def to_inet_string(self):
		inet_str = ''

		for d in self.detectors:
			inet_str += d.to_inet_string() + '\n\n'

		return inet_str
	
	def print_detectors(self, steps = 0):
		measurements = [d.get_measurements() for d in self.detectors]
		# labels = [d.get_labels() for d in self.detectors]
		ntd = len(measurements[0].T)
		nvd = len(measurements[1].T)
		fig, ax = plt.subplots(constrained_layout=True, nrows=nvd+1, figsize=(6, 6))
		fig.axes[0].matshow(measurements[0].T, cmap='gray', aspect='auto')
		fig.axes[0].set_xticks(np.arange(-.5, steps, 1), minor=True)
		fig.axes[0].set_yticks(np.arange(-.5, ntd, 1), minor=True)
		fig.axes[0].grid(which='minor', color='gray', linestyle='-', linewidth=2)
		fig.axes[0].xaxis.set_major_locator(ticker.MultipleLocator(1))

		for i in range(nvd):
			fig.axes[i+1].plot(measurements[1][:,i])
			fig.axes[i+1].set_ylabel("Voltage "+str(i+1))
			fig.axes[i+1].set_ylim(top=(max(measurements[1].T[i])+0.5))
			fig.axes[i+1].grid(b=None, which='major')
			fig.axes[i+1].xaxis.set_major_locator(ticker.MultipleLocator(1))
		plt.show()
