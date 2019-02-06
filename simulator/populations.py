import numpy as np

class Population():

	def __init__(self, neurons):
		# Build arrays with neurons properties
		self.I = np.array([neuron.I for neuron in neurons])
		self.out = np.array([neuron.out for neuron in neurons])
		self.m = np.array([neuron.m for neuron in neurons])
		self.V_reset = np.array([neuron.V_reset for neuron in neurons])
		self.V = np.array([neuron.V for neuron in neurons])
		self.thr = np.array([neuron.thr for neuron in neurons])
		self.amplitude = np.array([neuron.amplitude for neuron in neurons])
		self.I_e = np.array([neuron.I_e for neuron in neurons])

	def step(self):
		self.V = self.V * self.m + self.I # update V
		self.I = self.I_e # reset I with I_e
		self.out = np.zeros_like(self.amplitude)
		spiked = np.where(np.greater(self.V, self.thr))[0] # find spiking neurons
		self.out[spiked] = self.amplitude[spiked] # send output
		self.V[spiked] = self.V_reset[spiked] # reset voltage
