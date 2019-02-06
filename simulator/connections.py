import numpy as np
import simulator as SNN

class Synapse():
	"""Connection between two neurons

    Parameters
    ----------
    pre : Neuron
        Presynaptic neuron
    post : Neuron
        Postsynaptic neuron
    w : float
    	Connection weight
	d : int
		Synaptic delay (number of timesteps)
	"""

	def __init__(self, pre, post, w, d):
		self.pre = pre
		self.post = post
		self.w = np.array([w])
		size = 1 if isinstance(pre, SNN.Neuron) else pre.size
		self.out_pre = np.zeros((d,size)) # store output of the presynaptic neuron during d timesteps
		self.index = 0

	def step(self):
		self.post.I += self.w @ self.out_pre[self.index,:] # add w*pre_{t-d} to post 
		self.out_pre[self.index,:] = np.atleast_1d(self.pre.out) # store current output of pre
		self.index = (self.index + 1) % len(self.out_pre)