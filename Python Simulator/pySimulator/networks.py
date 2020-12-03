import numpy as np
from .nodes import *
from .connections import *
import networkx as nx

class Network():
	"""Network containing a list of nodes and synapses

    Parameters
    ----------
    nodes : list
        List of nodes in the network
    synapses : list
        List of synapses connecting nodes
    """

	def __init__(self, nodes=[], synapses=[]):
		self.nodes = nodes
		self.synapses = synapses
		self.graph = nx.DiGraph()


	def createLIF(self, m, V_init=0, V_reset=0, V_min=0, thr=1, amplitude=1, I_e=0, noise=0, rng=None, ID=None, increment_count=True):
		self.graph.add_node(ID)
		node = LIF(m, V_init, V_reset, V_min, thr, amplitude, I_e, noise, rng, ID, increment_count)
		self.nodes.append(node)
		return node

	def createInputTrain(self, train, loop, ID=None, increment_count=True):
		node = InputTrain(train, loop, ID, increment_count)
		self.nodes.append(node)
		return node

	def createRandomSpiker(self, p, amplitude=1, rng=None, ID=None, increment_count=True):
		node = RandomSpiker(p, amplitude, rng, ID, increment_count)
		self.nodes.append(node)
		return node

	def createSynapse(self, pre, post, w, d, ID=None, increment_count=True):
		self.graph.add_edge(pre.ID, post.ID)
		synapse = Synapse(pre, post, w, d, ID, increment_count)
		self.synapses.append(synapse)
		return synapse


	def step(self):
		for node in self.nodes: # update all nodes
			node.step()
		for synapse in self.synapses: # update all synapses
			synapse.step()

	def update_rng(self, rng):
		for node in self.nodes:
			node.update_rng(rng)

	def to_inet_string(self):
		inet_str = ''

		for n in self.nodes:
			inet_str += n.to_inet_string() + '\n'

		inet_str += '\n'

		for s in self.synapses:
			inet_str += s.to_inet_string() + '\n'

		return inet_str
