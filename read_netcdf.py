import netCDF4
import matplotlib.pyplot as plt
import numpy as np
import seaborn as sns
import pandas as pd

precip_nc_file = '/Users/ingeborglianes/Documents/download.netcdf'
nc = netCDF4.Dataset(precip_nc_file, mode='r')

print(nc.variables)

nc.variables.keys()
lat = nc.variables['latitude'][:]
lon = nc.variables['longitude'][:]
time_var = nc.variables['time']
dtime = netCDF4.num2date(time_var[:],time_var.units)
swh = nc.variables['swh']


# a pandas.Series designed for time series of a 2D lat,lon grid
#swh_data = pd.Series(swh,index=dtime,lat,lon)

chosen_locations=[[6390,844],[6365,819],[6390,919],[6415,969],[6415,994],[6440,1019],
                  [6465,1019],[6515,1144]]
list_data=[]
#swh_data.to_csv('swh_data.csv',index=True, header=True)
list=[]
for s in range(len(swh)):
    for latI in range(len(lat)):
        for lonI in range(len(lon)):
            if(swh[s,latI,lonI]!="masked" and [int(lat[latI]*100),int(lon[lonI]*100)] in chosen_locations):
                list_data.append([s+1,lat[latI],lon[lonI],swh[s,latI,lonI]])

print(list_data)
sum_vals=[0]*60
avg_vals=[]
for value in list_data:
    for i in range(60):
        if value[0]==i+1:
            sum_vals[i]+=value[3]

for num in sum_vals:
    avg_vals.append(num/8)

indx=[]
for i in range(60):
    indx.append(i+1)

print(avg_vals)

indx=np.array(indx)
avg_vals=np.array(avg_vals)

d={"Time Period": indx, "Average Significant Wave Height": avg_vals}
pd_indx_vals=pd.DataFrame(d)

sns.set_style("darkgrid")
sns.lineplot(x="Time Period",y="Average Significant Wave Height",data=pd_indx_vals)
plt.show()


f=open("weather_desember.txt","w+")
for i in range(len(avg_vals)):
    f.write(str(i+1)+" "+str(avg_vals[i]))
    f.write("\n￿")
f.close


