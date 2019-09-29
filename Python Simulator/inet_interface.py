from pySimulator.simulators import * 
from pySimulator.nodes import *
from pySimulator.connections import *
from pySimulator.detectors import *
from pySimulator.networks import *
from pySimulator.populations import *

def
def main():

	# create Poisson and spike train inputs
	poisson_input = ps.PoissonGenerator(.05)

	spike_train = [0]*10
	spike_train[0] = 1
	train_input = ps.SpikeTrain(spike_train)


	# create 2 noisy LIF (Sandia) neurons
	A = ps.LIF(.96)
	B = ps.LIF(.94, noise=.02)

	# connect the input train to A, the Poisson generator to B, and A to B
	train_A = ps.Synapse(train_input, A, w=.5, d=1)
	poisson_B = ps.Synapse(poisson_input, B, w=.5, d=1)
	A_B = ps.Synapse(A,B, w=.7, d=1)

	# create the network containing the nodes and the connections
	net = ps.Network([poisson_input, train_input,A,B], [train_A, poisson_B, A_B])
	
	# create recording devices
	raster = ps.Raster([poisson_input, train_input, A, B])
	multimeter = ps.Multimeter([A,B])

	# create and run simulator for 100 steps
	sim = ps.Simulator(net, [raster, multimeter])
	sim.run(100)

	# plot recordings
	raster.plot()
	multimeter.plot()




if __name__ == '__main__':
	#main()
	pass