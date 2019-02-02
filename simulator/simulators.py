class Simulator():
	"""Simulator

    Parameters
    ----------
    network : Network
        Network to simulate
	"""

	def __init__(self, network):
		self.network = network

	def run(self, steps, multimeter=True, raster=True):
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
		if raster:
			self.raster = np.zeros((steps, len(self.network.neurons)), dtype=bool)

		for i in range(steps):
			self.network.step()

			if multimeter:
				self.multimeter[i,:] = 	[neuron.V for neuron in self.network.neurons]
			if raster:
				self.raster[i,:] = 		[neuron.out > 0 for neuron in self.network.neurons]
