import numpy as np

"""
neurons
"""
class AbstractNeuron():
	""" Abstract class for a neuron

	Attributes
	----------
	I : float (Default: 0)
		Current membrane voltage of the neuron
	out : float (Default: 0)
		Current neuron output
	"""

	def __init__(self, amplitude=1):
		self.amplitude = amplitude
		self.I = 0
		self.out = 0
		self.V = 0

	def update_rng(self, rng):
		if hasattr(self, 'rng'):
			self.rng = rng


class LIF(AbstractNeuron):
	"""Leaky integrate-and-fire based on the Sandia model

	Parameters
	----------
	m : float
		Leakage constant
	V_init : float
		Initial membrane voltage (Default: 0)
	V_reset : float
		Reset voltage when the neuron has spiked (Default: 0)
	thr : float
		Spiking threshold (Default: 1)
	amplitude : float (Default: 1)
		Amplitude of the output when the neuron spikes
	I_e : float (Default: 0)
		Constant input current
	noise : float (Default: 0)
		Standard deviation of the normal distribution that is sampled from 
		to add noise to the membrane voltage at each step
	rng : np.random.RandomState
		Random generator for the noise
	"""

	count = 0

	def __init__(self, m, V_init=0, V_reset=0, V_min=0, thr=1, amplitude=1, I_e=0, noise=0, rng=None, ID=None, increment_count=True):
		AbstractNeuron.__init__(self, amplitude)
		self.m = m
		self.V = V_init
		self.V_reset = V_reset
		self.V_min = V_min
		self.thr = thr
		self.I_e = I_e
		self.rng = rng if rng != None else np.random.RandomState()
		self.noise = noise
		
		if ID is None:
			self.ID = LIF.count + 1
		else:
			self.ID = ID
		if increment_count:
			LIF.count += 1
		

	def step(self):
		self.V = self.V * self.m + self.I # update V
		if self.noise > 0:
			self.V += self.rng.normal(scale=self.noise) # add noise
		self.V = max(self.V_min, self.V)
		self.I = self.I_e # reset I with I_e
		if self.V > self.thr: # check for spike
			self.V = self.V_reset
			self.out = self.amplitude
		else:
			self.out = 0

	def to_inet_string(self):
		return self.__class__.__name__+'_'+str(self.ID)+' = ' \
					'network.create'+self.__class__.__name__+'('+ \
					str(self.m)+', '+ \
					str(self.V)+', '+ \
					str(self.V_reset)+', '+ \
					str(self.V_min)+', '+ \
					str(self.thr)+', '+ \
					str(self.amplitude)+', '+ \
					str(self.I_e)+', '+ \
					str(self.noise) \
				+')'



"""
Generators
"""
class InputTrain(AbstractNeuron):
	"""Generator that outputs a given train of events

	Parameters
	----------
	train : array_like
		Output train
	loop : boolean
		If true, the train will be looped. Otherwise, the output is 0 at the end of the train.
	"""

	count = 0

	def __init__(self, train, loop, ID=None, increment_count=True):
		AbstractNeuron.__init__(self, 1)
		self.train = train
		self.loop = loop
		self.size = len(train)
		self.index = 0
		
		if ID is None:
			self.ID = InputTrain.count + 1
		else:
			self.ID = ID
		if increment_count:
			InputTrain.count += 1


	def step(self):
		if self.index >= self.size:
			if self.loop:
				self.index = 0
				self.V = self.train[self.index]
			else:
				self.V = 0
			
		else:
			self.V = self.train[self.index]

		self.out = self.V
		self.index += 1

	def to_inet_string(self):
		return self.__class__.__name__+'_'+str(self.ID)+' = ' \
					'network.create'+self.__class__.__name__+'('+ \
					str(self.train)+', '+ \
					str(self.loop) \
				+')'

'''class PoissonGenerator(AbstractNeuron):
	"""Generator that fires with Poisson statistics, i.e. exponentially 
	distributed interspike intervals.

	Parameters
	----------
	I_e : float (Default: 0)
		Constant input current
	amplitude : float (Default: 1)
		Amplitude of the output
	rng : np.random.RandomState
		Random generator
	"""

	def __init__(self, I_e=0, amplitude=1, rng=None):
		AbstractNeuron.__init__(self, amplitude)
		self.I_e = I_e
		self.rng = rng if rng != None else np.random.RandomState()

	def step(self):
		self.V = 0
		if self.I > 0:
			while (-np.log(1-self.rng.rand()) / self.I) < 1:
				self.V += 1
			
		self.out = self.V * self.amplitude
		self.I = self.I_e
'''

	

class RandomSpiker(AbstractNeuron):
	"""Generator that fires with a given probability at each time step

	Parameters
	----------
	I_e : float (Default: 0)
		Constant input current
	amplitude : float (Default: 1)
		Amplitude of the output
	rng : np.random.RandomState
		Random generator
	"""

	count = 0

	def __init__(self, p, amplitude=1, rng=None, ID=None, increment_count=True):
		AbstractNeuron.__init__(self, amplitude)
		self.p = p
		self.rng = rng if rng != None else np.random.RandomState()

		if ID is None:
			self.ID = RandomSpiker.count + 1
		else:
			self.ID = ID
		if increment_count:
			RandomSpiker.count += 1

	def step(self):
		self.V = 0
		if self.rng.rand() < self.p:
			self.V = self.amplitude
			
		self.out = self.V

	def to_inet_string(self):
		return self.__class__.__name__+'_'+str(self.ID)+' = ' \
					'network.create'+self.__class__.__name__+'('+ \
					str(self.p)+', '+ \
					str(self.amplitude) \
				+')'

