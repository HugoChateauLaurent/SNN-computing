import numpy as np

class Neuron():
	"""General class for a neuron

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
		Constant background current

	
	Attributes
	----------
	I : float (Default: 0)
		Current membrane voltage of the neuron
	out : float (Default: 0)
		Current neuron output
	"""

	I = 0
	out = 0

	def __init__(self, m, V_init=0., V_reset=0., thr=1., amplitude=1., I_e=0.):
		self.m = m
		self.V = V_init
		self.V_reset = V_reset
		self.thr = thr
		self.amplitude = amplitude
		self.I_e = I_e
		

	def step(self):
		self.V = self.V * self.m + self.I # update V
		self.I = self.I_e # reset I with I_e
		if self.V > self.thr: # check for spike
			self.V = self.V_reset
			self.out = self.amplitude
		else:
			self.out = 0.

class SpikeTrain(Neuron):

	def __init__(self, train):
		self.train = train
		self.size = len(train)
		self.index = 0

	def step(self):
		self.out = self.train[self.index]
		self.index = (self.index + 1) % self.size

class PoissonGenerator(Neuron):

	def __init__(self, I_e=0., amplitude=1., seed=1):
		self.I_e = I_e
		self.amplitude = amplitude
		self.rng = np.random.RandomState(seed=seed)

	def step(self):
		self.out = 0
		if self.I > 0:
			while (-np.log(1.0-self.rng.rand()) / self.I) < 1:
				self.out += 1
			

		self.I = self.I_e

class Parrot(Neuron):
	def __init__(self, I_e=0., amplitude=1.):
		self.I_e = I_e
		self.amplitude = amplitude

	def step(self):
		self.out = self.I
		self.I = self.I_e

