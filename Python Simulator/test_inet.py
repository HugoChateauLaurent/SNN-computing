import pySimulator as ps
import numpy as np
import open_inet
import matplotlib.pyplot as plt


def main():

	network, simulator = open_inet.load("pySimulator/inet_files/my_network.inet")

	simulator.run(20)

	simulator.detectors[0].plot()
	plt.show()

if __name__ == '__main__':
	main()
