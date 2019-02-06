import simulator as SNN
import numpy as np

def main():

	'''A = Neuron(.009, I_e=.2)
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
				plt.show()'''

	'''duration = 100

	A = SNN.Neuron(.99, I_e=0)
	B = SNN.Neuron(.99, I_e=0.1)
	input = np.zeros((10))
	input[0] = 1
	G = SNN.SpikeTrain(input)

	net = SNN.Network([B], [], [G])
	sim = SNN.Simulator(net)

	
	sim.run(duration)

	plt.figure()
	for i in range(len(net.neurons)):
		plt.subplot(len(net.neurons),1,1+i)
		plt.plot(sim.multimeter[:,i])
		plt.ylabel("Voltage "+str(i+1))
		plt.ylim(top=net.neurons[i].thr)
		#plt.xticks(range(duration))

	plt.xlabel("Step")
	plt.show()

	plt.figure(figsize=(18,6))
	plt.matshow(sim.raster.T, cmap='gray', fignum=1)
	plt.ylabel("Neurons")
	plt.xlabel("Step")
	plt.show()'''


	

	#input
	if True:
		input = SNN.PoissonGenerator(.05)
	else:
		spike_train = [0]*10
		spike_train[0] = 1
		input = SNN.SpikeTrain(spike_train)


	A = SNN.Neuron(.96, noise=.005)
	in_A = SNN.Synapse(input, A, w=.5, d=1)

	B = SNN.Neuron(.94, noise=.02)
	A_B = SNN.Synapse(A,B, w=.7, d=1)

	net = SNN.Network([input,A,B], [in_A, A_B])
	

	raster = SNN.Raster([input,A,B])
	multimeter = SNN.Multimeter([A,B])

	sim = SNN.Simulator(net, [raster, multimeter])
	sim.run(100)

	print(raster.__dict__)

	raster.plot()
	multimeter.plot()




if __name__ == '__main__':
	main()