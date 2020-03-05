import cdsapi

c = cdsapi.Client()

c.retrieve(
    'reanalysis-era5-single-levels',
    {
        'product_type': 'reanalysis',
        'variable': 'significant_height_of_combined_wind_waves_and_swell',
        'year': '2019',
        'month': [
            '09'
        ],
        'day': [
            '01', '02', '03',
            '04', '05',
        ],
        'area': [65.25, 8.20, 63.4, 11.7], # North, West, South, East. Default: global
        'grid': [0.25, 0.25], # Latitude/longitude grid: east-west (longitude) and north-south resolution (latitude). Default: 0.25 x 0.25
        'time': [
            '07:00', '08:00',
            '09:00', '10:00', '11:00',
            '12:00', '13:00', '14:00',
            '15:00', '16:00', '17:00',
            '18:00',
        ],
        'format': 'netcdf',
    },
    'download_september.netcdf')


