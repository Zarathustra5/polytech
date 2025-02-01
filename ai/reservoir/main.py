#import reservoirpy as rpy
#rpy.verbosity(0)  # no need to be too verbose here
#rpy.set_seed(42)  # make everything reproducible!

from reservoirpy.nodes import Reservoir, Ridge 
import numpy as np
import matplotlib.pyplot as plt

# Create a Reservoir
reservoir = Reservoir(100, lr=0.5, sr=0.9)

# Initialize and run the reservoir
X = np.sin(np.linspace(0, 6*np.pi, 100)).reshape(-1, 1)

plt.figure(figsize=(10, 3))
plt.title("A sine wave.")
plt.ylabel("$sin(t)$")
plt.xlabel("$t$")
plt.plot(X)
plt.show()

# Call on a single timestep
s = reservoir(X[0].reshape(1, -1))

print("New state vector shape: ", s.shape)

states = np.empty((len(X), reservoir.output_dim))
for i in range(len(X)):
    states[i] = reservoir(X[i].reshape(1, -1))

plt.figure(figsize=(10, 3))
plt.title("Activation of 20 reservoir neurons.")
plt.ylabel("$reservoir(sin(t))$")
plt.xlabel("$t$")
plt.plot(states[:, :20])
plt.show()

# Create a readout
readout = Ridge(ridge=1e-7)

# Define a training task
X_train = X[:50]
Y_train = X[1:51]

plt.figure(figsize=(10, 3))
plt.title("A sine wave and its future.")
plt.xlabel("$t$")
plt.plot(X_train, label="sin(t)", color="blue")
plt.plot(Y_train, label="sin(t+1)", color="red")
plt.legend()
plt.show()

# Train the readout
train_states = reservoir.run(X_train, reset=True)
readout = readout.fit(train_states, Y_train, warmup=10)

test_states = reservoir.run(X[50:])
Y_pred = readout.run(test_states)

plt.figure(figsize=(10, 3))
plt.title("A sine wave and its future.")
plt.xlabel("$t$")
plt.plot(Y_pred, label="Predicted sin(t)", color="blue")
plt.plot(X[51:], label="Real sin(t+1)", color="red")
plt.legend()
plt.show()
