import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from statsmodels.tsa.statespace.sarimax import SARIMAX
from statsmodels.graphics.tsaplots import plot_acf, plot_pacf


# Visualize the original data
plt.figure(figsize=(12, 6))
plt.plot(combined_df.index, combined_df['Amount_normalized'], label='Original Data')
plt.xlabel('Date')
plt.ylabel('Amount')
plt.title('Original Time Series Data')
plt.legend()
plt.grid(True)
plt.show()

# Determine seasonal parameters using ACF and PACF plots
plt.figure(figsize=(12, 6))
plot_acf(combined_df['Amount_normalized'], lags=20, ax=plt.gca())
plt.title('Autocorrelation Function (ACF)')
plt.xlabel('Lag')
plt.ylabel('ACF')
plt.show()

plt.figure(figsize=(12, 6))
plot_pacf(combined_df['Amount_normalized'], lags=20, ax=plt.gca())
plt.title('Partial Autocorrelation Function (PACF)')
plt.xlabel('Lag')
plt.ylabel('PACF')
plt.show()

# Fit SARIMA model
# Example: SARIMA(1, 1, 1)x(1, 1, 1, 12) for seasonal_order
order = (1, 1, 1)  # Non-seasonal parameters (p, d, q)
seasonal_order = (1, 1, 1, 12)  # Seasonal parameters (P, D, Q, S)
sarima_model = SARIMAX(combined_df['Amount_normalized'], order=order, seasonal_order=seasonal_order, enforce_stationarity=False)
sarima_fit = sarima_model.fit(disp=False)

# Print model summary
print(sarima_fit.summary())

# Forecast future values
forecast_steps = 12  # Forecasting 12 steps ahead
forecast = sarima_fit.get_forecast(steps=forecast_steps)

# Plot forecasted values
forecast_index = pd.date_range(start=combined_df.index[-1] + pd.Timedelta(days=1), periods=forecast_steps, freq='D')
plt.figure(figsize=(12, 6))
plt.plot(combined_df.index, combined_df['Amount_normalized'], label='Original Data')
plt.plot(forecast_index, forecast.predicted_mean, label='Forecast')
plt.xlabel('Date')
plt.ylabel('Amount')
plt.title('SARIMA Forecast')
plt.legend()
plt.grid(True)
plt.show()
