import numpy as np

class Network():
	"""Network containing a list of nodes and synapses

    Parameters
    ----------
    nodes : list
        List of nodes in the network
    synapses : list
        List of synapses connecting nodes
    """

	def __init__(self, nodes, synapses):

		'''self.matricize = matricize

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

		else:'''
	
		self.nodes = nodes
		self.synapses = synapses

	def step(self):
		for node in self.nodes: # update all nodes
			node.step()
		for synapse in self.synapses: # update all synapses
			synapse.step()

	def update_rng(self, rng):
		for node in self.nodes:
			node.update_rng(rng)
