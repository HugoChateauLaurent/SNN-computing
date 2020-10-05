import numpy as np
import matplotlib.pyplot as plt
from matplotlib.ticker import MaxNLocator


class Raster():

	count = 0

	def __init__(self, targets=[], ID=None, increment_count=True):
		self.targets = targets

		if ID is None:
			self.ID = Raster.count + 1
		else:
			self.ID = ID
		if increment_count:
			Raster.count += 1

	def initialize(self, steps):
		self.spikes = np.zeros((steps, len(self.targets)), dtype=bool)
		self.index = 0

	def step(self):
		self.spikes[self.index,:] = [target.out > 0 for target in self.targets]
		self.index += 1

	def get_measurements(self):
		return self.spikes
	
	def get_labels(self):
		return [t.ID for t in self.targets]

	def addTarget(self, target):
		self.targets.append(target)

	def to_inet_string(self):
		inet_string = self.__class__.__name__+'_'+str(self.ID)+' = ' \
						'simulator.create'+self.__class__.__name__+'()'

		for t in self.targets:
			inet_string += self.__class__.__name__+'_'+str(self.ID)+'.addTarget('+t.__class__.__name__+'_'+str(t.ID)+')\n'

		return inet_string


class Multimeter():

	count = 0

	def __init__(self, targets=[], ID=None, increment_count=True):
		self.targets = targets

		if ID is None:
			self.ID = Multimeter.count + 1
		else:
			self.ID = ID
		if increment_count:
			Multimeter.count += 1

	def initialize(self, steps):
		self.V = np.zeros((steps, len(self.targets)))
		self.index = 0

	def step(self):
		self.V[self.index,:] = 	[target.V for target in self.targets]
		self.index += 1

	def get_measurements(self):
		return self.V
	
	def get_labels(self):
		return [t.ID for t in self.targets]

	def addTarget(self, target):
		self.targets.append(target)

	def to_inet_string(self):
		inet_string = self.__class__.__name__+'_'+str(self.ID)+' = ' \
						'simulator.create'+self.__class__.__name__+'()\n'
					
		for t in self.targets:
			inet_string += self.__class__.__name__+'_'+str(self.ID)+'.addTarget('+t.__class__.__name__+'_'+str(t.ID)+')\n'

		return inet_string