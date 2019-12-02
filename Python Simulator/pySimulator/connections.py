import numpy as np

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

	count = 0

	def __init__(self, pre, post, w, d, ID=None, increment_count=True):
		if(d<1):
			raise ValueError("Synaptic delay must be at least 1")
		self.pre = pre
		self.post = post
		self.w = w
		self.out_pre = np.zeros((d)) # store output of the presynaptic neuron during d timesteps
		self.index = 0

		if ID is None:
			self.ID = Synapse.count + 1
		else:
			self.ID = ID
		if increment_count:
			Synapse.count += 1

	def step(self):

		self.out_pre[self.index] = self.pre.out # store current output of pre
		self.index = (self.index + 1) % len(self.out_pre)
		self.post.I += self.w * self.out_pre[self.index] # add w*pre_{t-d} to post 

	def to_inet_string(self):
		return 'network.create'+self.__class__.__name__+'('+ \
					self.pre.__class__.__name__+'_'+str(self.pre.ID)+', '+ \
					self.post.__class__.__name__+'_'+str(self.pre.ID)+', '+ \
					str(self.w)+', '+ \
					str(self.out_pre.shape[0]) \
				+')'