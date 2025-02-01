from reservoirpy.datasets import mackey_glass
from reservoirpy.nodes import Reservoir, Ridge
from reservoirpy.observables import rmse, rsquare

# Step 1: Load the dataset
X = mackey_glass(n_timesteps=2000)

# Step 2: Create an Echo State Network
reservoir = Reservoir(units=100, lr=0.3, sr=1.25)
readout = Ridge(output_dim=1, ridge=1e-5)

esn = reservoir >> readout

# Step 3: Fit and run the ESN
esn.fit(X[:500], X[1:501], warmup=100)
predictions = esn.run(X[501:-1])
#predictions = esn.fit(X[:500], X[1:501]).run(X[501:-1])

# Step 4: Evaluate the ESN
print("RMSE:", rmse(X[502:], predictions), "R^2 score:", rsquare(X[502:], predictions))
