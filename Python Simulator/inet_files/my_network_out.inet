inet['network'] = Network()
inet['simulator'] = Simulator(network)
network = inet['network']
simulator = inet['simulator']

RandomSpiker_1 = network.createRandomSpiker(5.0, 5.0)
LIF_1 = network.createLIF(0.9, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0)
LIF_2 = network.createLIF(0.9, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0)

network.createSynapse(RandomSpiker_1, LIF_1, 1.0, 1)

Multimeter_1 = simulator.createMultimeter()
Multimeter_1.addTarget(LIF_1)
Multimeter_1.addTarget(LIF_2)


