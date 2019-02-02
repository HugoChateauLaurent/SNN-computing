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

	duration = 100

	A = Neuron(.99, I_e=0)
	B = Neuron(.99, I_e=0.1)
	input = np.zeros((10))
	input[0] = 1
	G = Generator(input)

	net = Network([B], [], [G])
	sim = Simulator(net)

	
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
	plt.show()




if __name__ == '__main__':
	main()