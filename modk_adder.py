import pySimulator as SNN
import numpy as np
import matplotlib.pyplot as plt

def main():

	# define k
	K=4
	
	# create constant input
	constant_one = [0]*1
	constant_one[0] = 1
	Const = SNN.SpikeTrain(constant_one)

	# create clock
	Clock = SNN.LIF(thr=K, V_reset=0, m=1)
	
	# create inputs
	A = SNN.LIF(thr=1, V_reset=0, m=1)
	B = SNN.LIF(thr=1, V_reset=0, m=1)

	# connect the clock to A and B to generate delay-coded mod 4 numbers
	Clock_A = SNN.Synapse(Clock, A, w=1, d=2)
	Clock_B = SNN.Synapse(Clock, B, w=1, d=3)
	
	# create HoldK neurons AH and BH -- ideally this should be a library motiv
	AH = SNN.LIF(thr=2, V_reset=0, m=0)
	A_AH = SNN.Synapse(A, AH, w=1, d=1)
	Const_AH = SNN.Synapse(Const, AH, w=1, d=1)
	Clock_AH = SNN.Synapse(Clock, AH, w=-1, d=K)
	# not sure if this self loop can be there or whether we need an additional neuron 
	#  in between with the same effect?
	AH_AH = SNN.Synapse(AH, AH, w=1, d=1)
	
	BH = SNN.LIF(thr=2, V_reset=0, m=0)
	B_BH = SNN.Synapse(B, BH, w=1, d=1)
	Const_BH = SNN.Synapse(Const, BH, w=1, d=1)
	Clock_BH = SNN.Synapse(Clock, BH, w=-1, d=K)
	BH_BH = SNN.Synapse(BH, BH, w=1, d=1)

	# create the adder
	Add = SNN.LIF(thr=K, V_reset=0, m=1)
	AH_Add = SNN.Synapse(AH, Add, w=-1, d=1)
	BH_Add = SNN.Synapse(BH, Add, w=-1, d=1)
	Const_Add = SNN.Synapse(Const, Add, w=2, d=1)
	Clock_Add = SNN.Synapse(Clock, Add, w=-K, d=K)
	
	# create the network containing the nodes and the connections
	net = SNN.Network([Const, Clock, A, B, AH, BH, Add], [A_AH, Const_AH, Clock_AH, AH_AH, B_BH, Const_BH, Clock_BH, BH_BH, AH_Add,BH_Add, Const_Add, Clock_Add])
	
	# create recording devices
	raster = SNN.Raster([A, B, AH, BH])
	multimeter = SNN.Multimeter([Add])

	# create and run simulator for 100 steps
	sim = SNN.Simulator(net, [raster, multimeter])
	sim.run(100)

	# plot recordings
	raster.plot()
	plt.show()
	multimeter.plot()
	plt.show()




if __name__ == '__main__':
	main()
