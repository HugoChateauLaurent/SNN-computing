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
		steps, count = self.spikes.shape
		# slightly increase width and scale height to amount of targets
		# default figsize is 6.4*4.8 inches
		fig = plt.figure(figsize=(10,count))
		x = np.array(range(steps))
		for idx in range(count):
			# get target spikes and make 1D
			y = self.spikes[:, idx].flatten()
			indices = y.nonzero()
			# plot each spike as one line
			plt.scatter(
				x[indices], y[indices] + idx,
				 s=600, marker="|", color="black"
			)
			# orientation horizontal baseline, could replace with grid
			plt.axhline(1+idx, color="black")

			# formatting
			plt.xticks(x)
			plt.ylim((0.5, count+0.5))
			plt.xlabel("Steps")
			plt.ylabel("Targets")
			# use ID of targets as Y-Ticks
			plt.yticks(range(1,count+1), [t.ID for t in self.targets])
		

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
		# slightly increase width and scale height to amount of targets
		steps, count = self.V.shape
		fig, ax = plt.subplots(count, 1, figsize=(10,2*count), sharex=True)
		ax.flatten()
		for idx in range(count):
			target = self.targets[idx]
			ca = ax[idx]
			ca.plot(self.V[:, idx], "o-", color="black")

			# formatting
			ca.set_ylabel("Voltage " + target.ID)
			padding = .1 * target.thr
			top = target.thr + padding
			bot = target.V_min - padding
			ca.set_ylim((bot, top))
			ca.grid(b=True, which='major', color='#d3d3d3', linestyle='-')
			ca.set_xticks(range(steps))
			ca.set_yticks(range(target.V_min, target.thr + 1))

		ca.set_xlabel("Step")

	def addTarget(self, target):
		self.targets.append(target)

	def to_inet_string(self):
		inet_string = self.__class__.__name__+'_'+str(self.ID)+' = ' \
						'simulator.create'+self.__class__.__name__+'()\n'
					
		for t in self.targets:
			inet_string += self.__class__.__name__+'_'+str(self.ID)+'.addTarget('+t.__class__.__name__+'_'+str(t.ID)+')\n'

		return inet_string
