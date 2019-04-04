import numpy as np
import matplotlib.pyplot as plt


class Raster():
	def __init__(self, targets):
		self.targets = targets

	def init(self, steps):
		self.spikes = np.zeros((steps, len(self.targets)), dtype=bool)
		self.index = 0

	def step(self):
		self.spikes[self.index,:] = [target.out > 0 for target in self.targets]
		self.index += 1

	def plot(self):
		plt.figure(figsize=(18,6))
		plt.matshow(self.spikes.T, cmap='gray', fignum=1)
		plt.ylabel("Targets")
		plt.xlabel("Step")
		plt.show()


class Multimeter():
	def __init__(self, targets):
		self.targets = targets

	def init(self, steps):
		self.V = np.zeros((steps, len(self.targets)))
		self.index = 0

	def step(self):
		self.V[self.index,:] = 	[target.V for target in self.targets]
		self.index += 1

	def plot(self):
		plt.figure(figsize=(18,6))
		for i in range(len(self.targets)):
			plt.subplot(len(self.targets),1,1+i)
			plt.plot(self.V[:,i])
			plt.ylabel("Voltage "+str(i+1))
			plt.ylim(top=self.targets[i].thr)
			#plt.xticks(range(duration))

		plt.xlabel("Step")
		plt.show()