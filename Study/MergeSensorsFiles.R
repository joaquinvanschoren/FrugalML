loadValues <- function() {
    pf1a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id1/measurements16_154856.txt", 
                     stringsAsFactors = FALSE)   
    summary(as.factor(pf1a$Activity))  
    
    pf2a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id2/measurements161910.txt", 
                     stringsAsFactors = FALSE)  
    summary(as.factor(pf2a$Activity)) 
    pf2b <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id2/measurements162314.txt",
                     stringsAsFactors = FALSE) 
    summary(as.factor(pf2b$Activity)) 
    
    pf3a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id3/measurements170048.txt", 
                     stringsAsFactors = FALSE) 
    summary(as.factor(pf3a$Activity)) 
    
    pf4a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id4/measurements103920.txt", 
                     stringsAsFactors = FALSE)   
    summary(as.factor(pf4a$Activity)) 
    pf4b <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id4/measurements104023.txt",
                     stringsAsFactors = FALSE) 
    summary(as.factor(pf4b$Activity)) 
    pf4c <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id4/measurements105344.txt",
                     stringsAsFactors = FALSE) 
    summary(as.factor(pf4c$Activity)) 
    
    pf5a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id5/measurements01_135636.txt", 
                     stringsAsFactors = FALSE) 
    summary(as.factor(pf5a$Activity)) 
    
    pf5a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id5/measurements01_135912.txt", 
                     stringsAsFactors = FALSE) 
    summary(as.factor(pf5a$Activity)) 
    
    pf6a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id6/measurements14,105852.txt", 
                     stringsAsFactors = FALSE) 
    summary(as.factor(pf6a$Activity)) 
    
    pf7a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id7/measurements14_124024.txt", 
                     stringsAsFactors = FALSE) 
    summary(as.factor(pf7a$Activity)) 
    
    pf8a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id8/measurements14_133952.txt", 
                     stringsAsFactors = FALSE) 
    summary(as.factor(pf8a$Activity)) 
    
    pf9a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id9/measurements14_140924.txt", 
                     stringsAsFactors = FALSE) 
    summary(as.factor(pf9a$Activity)) 
    
    pf10a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id10/measurements14_143717.txt", 
                      stringsAsFactors = FALSE) 
    summary(as.factor(pf10a$Activity)) 
    
    pf11a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id11/measurements15_151057.txt", 
                      stringsAsFactors = FALSE) 
    summary(as.factor(pf11a$Activity)) 
    
    pf12a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id12/measurements15_174116.txt", 
                      stringsAsFactors = FALSE) 
    summary(as.factor(pf12a$Activity)) 
    
    pf13a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id13/measurements16_121819.txt", 
                      stringsAsFactors = FALSE) 
    summary(as.factor(pf13a$Activity)) 
    
    pf14a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id14/measurements16_124559.txt", 
                      stringsAsFactors = FALSE) 
    summary(as.factor(pf14a$Activity)) 
    
    pf15a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id15/measurements16_152756.txt", 
                      stringsAsFactors = FALSE) 
    summary(as.factor(pf15a$Activity)) 
    
    pf16a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id16/measurements17_121047.txt", 
                      stringsAsFactors = FALSE) 
    summary(as.factor(pf16a$Activity)) 
    
    pf17a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id17/measurements17_125151.txt", 
                      stringsAsFactors = FALSE) 
    summary(as.factor(pf17a$Activity)) 
    
    pf18a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id18/measurements18_124630.txt", 
                      stringsAsFactors = FALSE) 
    summary(as.factor(pf18a$Activity)) 
    
    pf19a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id19/measurements20_150509.txt", 
                      stringsAsFactors = FALSE) 
    summary(as.factor(pf19a$Activity)) 
    
    pf20a <- read.csv("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Results/id20/measurements20,182820.txt", 
                      stringsAsFactors = FALSE) 
    summary(as.factor(pf20a$Activity)) 
    
    
    dsx <- rbind(pf1a, pf2a, pf2b, pf3a, pf4a, pf4b, pf4c, pf5a, pf6a, pf7a, pf8a, pf9a, pf10a, pf11a, pf12a, pf13a, pf14a, pf15a, pf16a, pf17a, pf18a, pf19a, pf20a) 
    
    return (dsx) 
}

dsx <- loadValues() 

dsx$Activity <- as.factor(dsx$Activity) 
dsx <- dsx[!is.na(dsx$HeartRateVal), ]  
dsx <- dsx[, 2:ncol(dsx)]       

calcNewFeatures <- function(originData) { 
    # compute new features 

    for (i in seq(32, nrow(originData), 16)) { 
        numStart <- i-31 
        originData[i, 20] <- min(originData[numStart:i, 1]) 
        originData[i, 21] <- min(originData[numStart:i, 2]) 
        originData[i, 22] <- min(originData[numStart:i, 3]) 
        originData[i, 23] <- max(originData[numStart:i, 1]) 
        originData[i, 24] <- max(originData[numStart:i, 2]) 
        originData[i, 25] <- max(originData[numStart:i, 3])  
        originData[i, 26] <- mean(originData[numStart:i, 1]) 
        originData[i, 27] <- mean(originData[numStart:i, 2]) 
        originData[i, 28] <- mean(originData[numStart:i, 3])  
        originData[i, 29] <- originData[i, 23] - originData[i, 20] 
        originData[i, 30] <- originData[i, 24] - originData[i, 21] 
        originData[i, 31] <- originData[i, 25] - originData[i, 22] 
        originData[i, 32] <- sd(originData[numStart:i, 1]) 
        originData[i, 33] <- sd(originData[numStart:i, 2]) 
        originData[i, 34] <- sd(originData[numStart:i, 3])  
         
        originData[i, 35] <- min(originData[numStart:i, 4]) 
        originData[i, 36] <- min(originData[numStart:i, 5]) 
        originData[i, 37] <- min(originData[numStart:i, 6]) 
        originData[i, 38] <- max(originData[numStart:i, 4]) 
        originData[i, 39] <- max(originData[numStart:i, 5]) 
        originData[i, 40] <- max(originData[numStart:i, 6])  
        originData[i, 41] <- mean(originData[numStart:i, 4]) 
        originData[i, 42] <- mean(originData[numStart:i, 5]) 
        originData[i, 43] <- mean(originData[numStart:i, 6])  
        originData[i, 44] <- originData[i, 38] - originData[i, 35] 
        originData[i, 45] <- originData[i, 39] - originData[i, 36] 
        originData[i, 46] <- originData[i, 40] - originData[i, 41] 
        originData[i, 47] <- sd(originData[numStart:i, 4]) 
        originData[i, 48] <- sd(originData[numStart:i, 5]) 
        originData[i, 49] <- sd(originData[numStart:i, 6])  
        
        originData[i, 50] <- min(originData[numStart:i, 7]) 
        originData[i, 51] <- min(originData[numStart:i, 8]) 
        originData[i, 52] <- min(originData[numStart:i, 9]) 
        originData[i, 53] <- max(originData[numStart:i, 7]) 
        originData[i, 54] <- max(originData[numStart:i, 8]) 
        originData[i, 55] <- max(originData[numStart:i, 9])  
        originData[i, 56] <- mean(originData[numStart:i, 7]) 
        originData[i, 57] <- mean(originData[numStart:i, 8]) 
        originData[i, 58] <- mean(originData[numStart:i, 9])  
        originData[i, 59] <- originData[i, 53] - originData[i, 50] 
        originData[i, 60] <- originData[i, 54] - originData[i, 51] 
        originData[i, 61] <- originData[i, 55] - originData[i, 52] 
        originData[i, 62] <- sd(originData[numStart:i, 7]) 
        originData[i, 63] <- sd(originData[numStart:i, 8]) 
        originData[i, 64] <- sd(originData[numStart:i, 9])  
        
        originData[i, 65] <- min(originData[numStart:i, 10]) 
        originData[i, 66] <- min(originData[numStart:i, 11]) 
        originData[i, 67] <- min(originData[numStart:i, 12]) 
        originData[i, 68] <- max(originData[numStart:i, 10]) 
        originData[i, 69] <- max(originData[numStart:i, 11]) 
        originData[i, 70] <- max(originData[numStart:i, 12])  
        originData[i, 71] <- mean(originData[numStart:i, 10]) 
        originData[i, 72] <- mean(originData[numStart:i, 11]) 
        originData[i, 73] <- mean(originData[numStart:i, 12])  
        originData[i, 74] <- originData[i, 68] - originData[i, 65] 
        originData[i, 75] <- originData[i, 69] - originData[i, 66] 
        originData[i, 76] <- originData[i, 70] - originData[i, 67] 
        originData[i, 77] <- sd(originData[numStart:i, 10]) 
        originData[i, 78] <- sd(originData[numStart:i, 11]) 
        originData[i, 79] <- sd(originData[numStart:i, 12])  
        
        originData[i, 80] <- min(originData[numStart:i, 13]) 
        originData[i, 81] <- min(originData[numStart:i, 14]) 
        originData[i, 82] <- min(originData[numStart:i, 15]) 
        originData[i, 83] <- max(originData[numStart:i, 13]) 
        originData[i, 84] <- max(originData[numStart:i, 14]) 
        originData[i, 85] <- max(originData[numStart:i, 15])  
        originData[i, 86] <- mean(originData[numStart:i, 13]) 
        originData[i, 87] <- mean(originData[numStart:i, 14]) 
        originData[i, 88] <- mean(originData[numStart:i, 15])  
        originData[i, 89] <- originData[i, 83] - originData[i, 80] 
        originData[i, 90] <- originData[i, 84] - originData[i, 81] 
        originData[i, 91] <- originData[i, 85] - originData[i, 82] 
        originData[i, 92] <- sd(originData[numStart:i, 13]) 
        originData[i, 93] <- sd(originData[numStart:i, 14]) 
        originData[i, 94] <- sd(originData[numStart:i, 15])  
        
        originData[i, 95] <- min(originData[numStart:i, 16]) 
        originData[i, 96] <- max(originData[numStart:i, 16]) 
        originData[i, 97] <- mean(originData[numStart:i, 16]) 
        originData[i, 98] <- originData[i, 96] - originData[i, 95] 
        originData[i, 99] <- sd(originData[numStart:i, 16]) 

        originData[i, 100] <- min(originData[numStart:i, 17]) 
        originData[i, 101] <- max(originData[numStart:i, 17]) 
        originData[i, 102] <- mean(originData[numStart:i, 17]) 
        originData[i, 103] <- originData[i, 101] - originData[i, 100] 
        originData[i, 104] <- sd(originData[numStart:i, 17]) 

        originData[i, 105] <- min(originData[numStart:i, 18]) 
        originData[i, 106] <- max(originData[numStart:i, 18]) 
        originData[i, 107] <- mean(originData[numStart:i, 18]) 
        originData[i, 108] <- originData[i, 106] - originData[i, 105] 
        originData[i, 109] <- sd(originData[numStart:i, 18]) 
    }  
    
    originData <- originData[!is.na(originData[ , 25]), c(20:109, 19)] 
    
    colnames(originData) <- c("AccelXMin", "AccelYMin", "AccelZMin", "AccelXMax", "AccelYMax", "AccelZMax", 
    "AccelXMean", "AccelYMean", "AccelZMean", "AccelXRange", "AccelYRange", "AccelZRange", 
    "AccelXStd", "AccelYStd", "AccelZStd", "GyroXMin", "GyroYMin", "GyroZMin", 
    "GyroXMax", "GyroYMax", "GyroZMax", "GyroXMean", "GyroYMean", "GyroZMean", 
    "GyroXRange", "GyroYRange", "GyroZRange", "GyroXStd", "GyroYStd", "GyroZStd", 
    "GravityXMin", "GravityYMin", "GravityZMin", "GravityXMax", "GravityYMax", "GravityZMax", 
    "GravityXMean", "GravityYMean", "GravityZMean", "GravityXRange", "GravityYRange", "GravityZRange", 
    "GravityXStd", "GravityYStd", "GravityZStd", "LinAccelXMin", "LinAccelYMin", "LinAccelZMin", 
    "LinAccelXMax", "LinAccelYMax", "LinAccelZMax", "LinAccelXMean", "LinAccelYMean", "LinAccelZMean", 
    "LinAccelXRange", "LinAccelYRange", "LinAccelZRange", "LinAccelXStd", "LinAccelYStd", "LinAccelZStd", 
    "RotVecXMin", "RotVecYMin", "RotVecSMin", "RotVecXMax", "RotVecYMax", "RotVecSMax", 
    "RotVecXMean", "RotVecYMean", "RotVecSMean", "RotVecXRange", "RotVecYRange", "RotVecSRange", 
    "RotVecXStd", "RotVecYStd", "RotVecSStd",
    "AiPreValMin", "AiPreValMax", "AiPreValMean", "AiPreValRange", "AiPreValStd",
    "MagFielYMin", "MagFielYMax", "MagFielYMean", "MagFielYRange", "MagFielYStd",
    "HeartRateValMin", "HeartRateValMax", "HeartRateValMean", "HeartRateValRange", "HeartRateValStd",
    "Activity") 
    
    return (extenData) 
} 

# sensor for heart rate measurements is not fast, and some values should be imputed 
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

# clean all activities with an empty label 
dsx <- as.data.frame(dsx[dsx$Activity != 6, ])   

# update data set with new values 
dsx <- calcNewFeatures(dsx) 

framesToRemove <- 5 
cutForLoop <- nrow(dsx) - framesToRemove 
numChanges <- 0 

# remove approximately a second before transition 
for (i in framesToRemove:cutForLoop) { 
    if (as.numeric(as.character(dsx$Activity[i])) != as.numeric(as.character(dsx$Activity[i-1])) & 
        as.numeric(as.character(dsx$Activity[i-1])) != 6) { 
        for (j in 1:framesToRemove - 1) {
            dsx$Activity[i - j] <- 6 
            dsx$Activity[i + j] <- 6 
        }
        numChanges <- numChanges + 1 
        
        print(i) 
        
    }
    if (i %% 100 == 0) {
        print(i) 
    } 
} 


# clean all activities with an empty label 
dsx <- as.data.frame(dsx[dsx$Activity != 6, ])   

# get factors with existing values 
dsx$Activity <- droplevels(dsx$Activity) 

# obtain raw values 
dsa <- dsx[ , c(1, 2, 3, 10, 11, 12, 19, 20, 21, 28, 29, 30, 37, 38, 40, 50, 54, 62, 65)] 

dsv <- dsa[, c(18, 16, 14, 9, 7, 15, 17, 10, 13, 8, 11, 12, 4, 5)] 

# load new features 

dsb <- readRDS("/home/mikhail/Desktop/GitProjects/FrugalML/Study/data/SensorsInformation/Features/extended/new_features_included.RDS") 

# present results in common notation 
options("scipen" = 100, "digits" = 5) 

# group values in a small data frame that can be used for a filter  
for (i in 1:64) {
    if (i == 1) {
        value.min <- min(dsx[, i]) 
        value.max <- max(dsx[, i]) 
    } else {
        value.min <- c(value.min, min(dsx[, i])) 
        value.max <- c(value.max, max(dsx[, i]))  
    }
} 
value.min <- as.data.frame(t(c(value.min, 0))) 
value.max <- as.data.frame(t(c(value.max, 5))) 

dsb <- rbind(value.min, value.max) 
colnames(dsb) <- colnames(dsx)  

# choose what file do you want to write 
write.csv(dsx, "measurements.csv", row.names = FALSE)  