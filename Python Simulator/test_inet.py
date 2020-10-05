import pySimulator as ps
import matplotlib.pyplot as plt
import argparse

def main():
	parser = argparse.ArgumentParser()
	parser.add_argument('-d', default=20, metavar='N', type=int, help='The amount of steps to simulate')
	parser.add_argument('-f', default="./stop_the_clock.inet", metavar='F', type=str, help='The amount of steps to simulate')
	args = parser.parse_args()

	# load the network (possibly created via the GUI)
	_net, sim = ps.inet_interface.load(args.f)

	# simulate 20 time steps
	sim.run(args.d)

if __name__ == '__main__':
	main()