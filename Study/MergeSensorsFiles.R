ds1 <- read.csv("//home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/measurements16.txt", 
                stringsAsFactors = FALSE)   
ds2 <- read.csv("//home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/measurements39.txt",
                stringsAsFactors = FALSE) 

dsx <- rbind(ds1, ds2) 

dsx$Activity <- as.factor(dsx$Activity) 
dsx <- dsx[!is.na(dsx$HeartRateVal), ]  
dsx <- dsx[, 2:ncol(dsx)]  

# sensor for heart rate measuremens is not fast, and some values should be imputed 
for (i in 1:nrow(dsx)) {
    if (is.na(dsx$HeartRateValMean[i])) {
        dsx$HeartRateValMean[i] <- dsx$HeartRateVal[i]          
    }
} 

for (i in 1:nrow(dsx)) {
    if (is.na(dsx$HeartRateValStd[i])) {
        dsx$HeartRateValStd[i] <- 0           
    }
} 

# remove approximately a second before transition 
for (i in 17:nrow(dsx)) { 
    if (dsx$Activity[i] != dsx$Activity[i-1]) {
        for (j in 1:16) {
            dsx$Activity[i - j] <- 6 
        }
    } 
} 

# clean all activities with an empty label 
dsx <- as.data.frame(dsx[dsx$Activity != 6, ])   

write.csv(dsx, "measurements.csv", row.names = FALSE) 
   