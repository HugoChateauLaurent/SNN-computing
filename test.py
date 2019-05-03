import pySimulator as SNN
import numpy as np

def main():

	# create Poisson and spike train inputs
	poisson_input = SNN.PoissonGenerator(.05)

	spike_train = [0]*10
	spike_train[0] = 1
	train_input = SNN.SpikeTrain(spike_train)


	# create 2 noisy LIF (Sandia) neurons
	A = SNN.LIF(.96)
	B = SNN.LIF(.94, noise=.02)

	# connect the input train to A, the Poisson generator to B, and A to B
	train_A = SNN.Synapse(train_input, A, w=.5, d=1)
	poisson_B = SNN.Synapse(poisson_input, B, w=.5, d=1)
	A_B = SNN.Synapse(A,B, w=.7, d=1)

	# create the network containing the nodes and the connections
	net = SNN.Network([poisson_input, train_input,A,B], [train_A, poisson_B, A_B])
	
	# create recording devices
	raster = SNN.Raster([poisson_input, train_input, A, B])
	multimeter = SNN.Multimeter([A,B])

	# create and run simulator for 100 steps
	sim = SNN.Simulator(net, [raster, multimeter])
	sim.run(100)

	# plot recordings
	raster.plot()
	multimeter.plot()




if __name__ == '__main__':
	main()