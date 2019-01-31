import numpy as np
import matplotlib.pyplot as plt




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
		self.w = w
		self.out_pre = np.zeros((d,)) # store output of the presynaptic neuron during d timesteps

	def step(self):
		self.post.I += self.w * self.out_pre[0] # add w*pre_{t-d} to post 
		self.out_pre = np.roll(self.out_pre, -1)
		self.out_pre[-1] = self.pre.out # store current output of pre
		





class Network():
	"""Network containing a list of neurons and synapses

    Parameters
    ----------
    neurons : list
        List of neurons in the network
    synapses : list
        List of synapses connecting neurons
    """

	def __init__(self, neurons, synapses):
		self.neurons = neurons
		self.synapses = synapses

	def step(self):
		for neuron in self.neurons: # update all neurons
			neuron.step() 
		for synapse in self.synapses: # update all synapses
			synapse.step()




class Simulator():
	"""Simulator

    Parameters
    ----------
    network : Network
        Network to simulate
	"""

	def __init__(self, network):
		self.network = network

	def run(self, steps, multimeter=True):
		"""Run the simulator

	    Parameters
	    ----------
	    steps : int
	        Number of steps to simulate
        multimeter : boolean (Default: True)
        	Whether or not to record the membrane voltages of each neuron
		"""
		
		# uncomment if implementing dt (but the project seems stepwise oriented)
		# steps = int(np.round(float(seconds) / self.dt))

		if multimeter:
			self.multimeter = np.zeros((steps, len(self.network.neurons)))

		for i in range(steps):
			self.network.step()

			if multimeter:
				self.multimeter[i,:] = [neuron.V for neuron in self.network.neurons]


def main():

	A = Neuron(.9, I_e=.2)
	B = Neuron(.9)
	C = Neuron(.9, I_e=.1)
	A_B = Synapse(A,B,w=.7,d=2)
	A_C = Synapse(A,C,w=.7,d=4)

	net = Network([A,B,C], [A_B,A_C])
	sim = Simulator(net)

	duration = 20
	sim.run(duration)

	plt.figure()
	for i in range(len(net.neurons)):
		plt.subplot(len(net.neurons),1,1+i)
		plt.plot(sim.multimeter[:,i])
		plt.ylabel("Voltage "+str(i+1))
		plt.ylim(top=net.neurons[i].thr)
		plt.xticks(range(duration))

	plt.xlabel("Step")
	plt.show()




if __name__ == '__main__':
	main()