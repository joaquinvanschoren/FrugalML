pf1a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Study/id1/measurements194537.txt", 
                stringsAsFactors = FALSE)   
summary(as.factor(pf1a$Activity))  
pf1b <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Study/id1/measurements160140.txt",
                stringsAsFactors = FALSE) 
summary(as.factor(pf1b$Activity)) 


pf2a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Study/id2/measurements161910.txt", 
                 stringsAsFactors = FALSE)  
summary(as.factor(pf2a$Activity)) 
pf2b <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Study/id2/measurements162314.txt",
                 stringsAsFactors = FALSE) 
summary(as.factor(pf2b$Activity)) 

pf3a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Study/id3/measurements170048.txt", 
                 stringsAsFactors = FALSE) 
summary(as.factor(pf3a$Activity)) 

pf4a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Study/id4/measurements103920.txt", 
                 stringsAsFactors = FALSE)   
summary(as.factor(pf4a$Activity)) 
pf4b <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Study/id4/measurements104023.txt",
                 stringsAsFactors = FALSE) 
summary(as.factor(pf4b$Activity)) 
pf4c <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Study/id4/measurements105344.txt",
                 stringsAsFactors = FALSE) 
summary(as.factor(pf4c$Activity)) 

dsx <- rbind(pf1a, pf1b, pf2a, pf2b, pf3a, pf4a, pf4b, pf4c) 

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

# present results in common notation 
options("scipen" = 100, "digits" = 5) 

write.csv(dsx, "measurements.csv", row.names = FALSE) 
