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

framesToRemove <- 80 + 1 
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
    if (i %% 10000 == 0) {
        print(i) 
    } 
} 
  
# clean all activities with an empty label 
dsx <- as.data.frame(dsx[dsx$Activity != 6, ])   
 
# present results in common notation 
options("scipen" = 100, "digits" = 5) 

write.csv(dsx, "measurements.csv", row.names = FALSE) 
