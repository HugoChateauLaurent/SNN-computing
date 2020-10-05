RandomSpiker_1 = RandomSpiker(5.0, 5.0)
LIF_1 = LIF(0.9, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0)
LIF_2 = LIF(0.9, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0)


Multimeter_1 = Multimeter([LIF_1, LIF_2])


Synapse_1 = Synapse(RandomSpiker_1, LIF_1, 1.0, 1)


inet['network'] = Network([RandomSpiker_1, LIF_1, LIF_2], [Synapse_1])
inet['simulator'] = Simulator(inet['network'], [Multimeter_1])
