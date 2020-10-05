inet['network'] = Network()
inet['simulator'] = Simulator(inet['network'])

# inet interface uses 'net' and 'sim'
net = inet['network']
sim = inet['simulator']

#define K
K=2

# create single spike programmed input, spiking at t=0 only
Programmed_one = net.createInputTrain([0,1], False) # the spike could be anything, but here it is of amplitude 2, not timestep 2

# create infinitely spiking neuron from t=1 onwards
Const = net.createLIF(thr=1, V_reset=1, m=1)
net.createSynapse(Programmed_one, Const, w=1, d=1)

# create clock which fire at point t=4 value 4
Clock = net.createLIF(thr=K, V_reset=0, m=1)
net.createSynapse(Const, Clock, w=1, d=1)

# create node Ah
Ah = net.createLIF(thr=1, V_reset=1, m=0.5)
net.createSynapse(Const, Ah, w=-5, d=K+2)

# create node Bh
Bh = net.createLIF(thr=1, V_reset=1, m=0.5)
net.createSynapse(Const, Bh, w=-5, d=K+2)

# create node Cin
Cin = net.createLIF(thr=1, V_reset=1, m=0.5)
net.createSynapse(Const, Cin, w=-5, d=K+2)

# create Add node
Add = net.createLIF(thr=K, V_reset=0, m=1)
net.createSynapse(Ah, Add, w=1, d=1)
net.createSynapse(Bh, Add, w=1, d=1)
net.createSynapse(Cin, Add, w=1, d=3)

# create Cout node
Cout = net.createLIF(thr=K, V_reset=0, m=1)
net.createSynapse(Add, Cout, w=-1, d=1)
net.createSynapse(Const, Cout, w=1, d=1)

# The two inputs and Cin, Low is encoded as d=2, High is encoded as d=1
net.createSynapse(Clock, Ah, w=1, d=2)
net.createSynapse(Clock, Bh, w=1, d=2)
net.createSynapse(Clock, Cin, w=1, d=2)

Raster_1 = sim.createRaster()
Raster_1.addTarget(Clock)
Raster_1.addTarget(Cout)

Multimeter_1 = sim.createMultimeter()
Multimeter_1.addTarget(Add)

