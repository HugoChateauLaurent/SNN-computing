import numpy as np
import matplotlib.pyplot as plt


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

	def plot(self):
		plt.matshow(self.spikes.T, cmap='gray', fignum=1)
		plt.ylabel("Targets")
		plt.xlabel("Step")

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

	def plot(self):
		for i in range(len(self.targets)):
			plt.subplot(len(self.targets),1,1+i)
			plt.plot(self.V[:,i])
			plt.ylabel("Voltage "+str(i+1))
			plt.ylim(top=self.targets[i].thr)
			#plt.xticks(range(duration))

		plt.xlabel("Step")

	def addTarget(self, target):
		self.targets.append(target)

	def to_inet_string(self):
		inet_string = self.__class__.__name__+'_'+str(self.ID)+' = ' \
						'simulator.create'+self.__class__.__name__+'()\n'
					
		for t in self.targets:
			inet_string += self.__class__.__name__+'_'+str(self.ID)+'.addTarget('+t.__class__.__name__+'_'+str(t.ID)+')\n'

		return inet_string
