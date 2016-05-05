ds1 <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/data/SensorsInformation/measurements0.txt") 
ds2 <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/data/SensorsInformation/measurements3.txt") 
ds3 <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/data/SensorsInformation/measurements6.txt") 
ds4 <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/data/SensorsInformation/measurements25.txt") 
ds5 <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/data/SensorsInformation/measurements42.txt") 
ds6 <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/data/SensorsInformation/measurements46.txt") 
ds7 <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/data/SensorsInformation/measurements73.txt") 
ds8 <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/data/SensorsInformation/measurements82.txt") 

dsx <- rbind(ds1, ds2, ds3, ds4, ds5, ds6, ds7, ds8) 

dsx$Activity <- as.factor(dsx$Activity) 

dsx <- dsx[dsx$Activity != 6, ]  
dsx <- dsx[!is.na(dsx$HeartRateVal), ]  
dsx <- dsx[, 2:24]  

write.csv(dsx, "measurements.csv", row.names = FALSE) 
   