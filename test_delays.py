import pySimulator as SNN
import numpy as np
import matplotlib.pyplot as plt

def main():

	spike_train = [0]*10
	spike_train[0] = 1

	train_input = SNN.SpikeTrain(spike_train)

	N = 5

	neurons = [SNN.LIF(1) for i in range(N)]
	synapses = [SNN.Synapse(train_input, neurons[i], d=i+1, w=2) for i in range(N)]

	
	# create the network containing the nodes and the connections
	net = SNN.Network([train_input]+neurons, synapses)
	
	# create recording devices
	raster = SNN.Raster([train_input]+neurons)

	sim = SNN.Simulator(net, [raster])
	sim.run(20)

	# plot recordings
	plt.figure(figsize=(18/3,6/3))
	raster.plot()
	plt.yticks(range(N+1), ['clock']+[x+1 for x in range(5)])
	plt.ylabel('Delay')

	plt.show()




if __name__ == '__main__':
	main()