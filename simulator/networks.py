class Network():
	"""Network containing a list of neurons and synapses

    Parameters
    ----------
    neurons : list
        List of neurons in the network
    synapses : list
        List of synapses connecting neurons
    """

	def __init__(self, neurons, synapses, generators=[]):

		self.matricize = matricize

		if matricize:

			# Build arrays with neurons properties
			self.I = np.array([neuron.I for neuron in neurons])
			self.out = np.array([neuron.out for neuron in neurons])
			self.m = np.array([neuron.m for neuron in neurons])
			self.V_reset = np.array([neuron.V_reset for neuron in neurons])
			self.V = np.array([neuron.V for neuron in neurons])
			self.thr = np.array([neuron.thr for neuron in neurons])
			self.amplitude = np.array([neuron.amplitude for neuron in neurons])
			self.I_e = np.array([neuron.I_e for neuron in neurons])

			# Build arrays with synapses properties
			w = np.zeros((len(neurons), len(neurons)))
			for synapse in synapses:
				w[neurons.index(synapse.post), neurons.index(synapse.pre)] += synapse.w
			self.w = w
			self.out_pre = np.zeros((d,))

		else:
			self.neurons = neurons
			self.synapses = synapses
			self.generators = generators

	def step(self):
		for neuron in self.neurons: # update all neurons
			neuron.step() 
		for generator in self.generators: # update all generators
			generator.step()
		for synapse in self.synapses: # update all synapses
			synapse.step()