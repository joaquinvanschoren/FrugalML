library("mlr") 
library("mlrMBO") 
library("OpenML") 


mlr::configureMlr(show.info = FALSE) 

loadTasks <- function() {
    tasksInStudy = c(3504, 3516, 3523, 3535, 3547, 3559, 3561, 3566, 3573, 3597, 3605, 3624, 3643, 3650, 
                     3655, 3667, 3674, 3701, 3713, 3725, 3756, 3768, 3782, 3908, 3910, 3922, 3939, 3953, 
                     2068, 3802, 3807, 3826, 3838, 3845, 3852, 3864, 3871, 3890, 3895, 3011, 9, 35, 2272, 
                     3648, 11, 3636, 3612, 3857, 3915, 2075, 3751, 3629, 3706, 3693, 3876, 3787, 3491, 47, 
                     3585, 3763, 3799, 3554, 3718, 3580, 3679, 3732, 3794, 3869, 3819, 23, 3496, 3578, 3814, 
                     3749, 3737, 3542, 3888, 3770, 3720, 3775, 3617, 3592, 3631, 3744, 2373, 4, 42, 54, 3903, 
                     3484, 30, 3681, 3883, 3821, 3662, 16, 3833, 3698, 3840, 28, 3686, 206, 59, 3600, 2273, 
                     2071, 5, 48, 3625, 3651, 3656, 3663, 3675, 3682, 3707, 3719, 3721, 3726, 3738, 3757, 
                     3790, 3500, 3517, 3524, 3536, 3543, 3548, 3550, 3562, 3579, 3904, 3942, 3803, 3808, 
                     3827, 3853, 3865, 3872, 3889, 3891, 3012, 3694, 3555, 3497, 3613, 3923, 3788, 3815, 
                     3752, 3810, 3909, 3637, 3771, 3860, 3649, 3620, 3574, 3769, 3911, 3644, 3699, 3632, 
                     3670, 3702, 3687, 50, 24, 55, 31, 29, 3884, 3764, 3492, 3733, 3776, 3598, 3916, 36, 
                     3877, 3795, 3581, 3714, 3822, 3839, 2076, 43, 3954, 3618, 3858, 3834, 3896, 3606, 
                     3586, 3668, 3740, 3485, 3567, 3846, 3783, 3512, 3601, 219, 3841, 3593, 12, 
                     3745, 2146, 51, 3570, 3621, 3626, 3652, 3669, 3722, 3746, 3760, 3791, 2274, 2065, 2077, 
                     3481, 3498, 3501, 3513, 3518, 3532, 3537, 3544, 3551, 3556, 3563, 3568, 3575, 3809, 
                     3823, 3830, 3835, 3847, 3861, 3866, 3873, 3878, 3880, 3892, 3912, 3943, 3950, 3640, 
                     3607, 3772, 3582, 3018, 3796, 3811, 3703, 18, 3727, 3683, 3955, 3493, 3715, 3664, 
                     3619, 3905, 3690, 3710, 3645, 3695, 3657, 3734, 3777, 3638, 3854, 3741, 3789, 
                     3897, 13, 49, 37, 3688, 32, 3917, 3671, 3816, 3842, 3594, 3486, 3549, 3708, 3602, 
                     3784, 3614, 3804, 3859, 3587, 3676, 3753, 3765, 3633, 2382, 3739, 3758, 3599, 3828, 
                     6, 2142, 20, 3885, 1, 145, 3646, 3665, 3696, 3704, 3709, 3723, 3728, 3754, 3761, 
                     3792, 2275, 2078, 3487, 3499, 3502, 3507, 3519, 3526, 3545, 3557, 3569, 3571, 3576, 
                     3595, 3021, 3906, 3951, 3805, 3824, 3829, 3831, 3848, 3855, 3862, 3881, 3886, 3893, 
                     2, 7, 33, 52, 57, 14, 45, 3730, 3817, 26, 3836, 3901, 3691, 3879, 3918, 3867, 3622, 
                     3634, 3747, 3639, 3797, 3019, 2073, 3920, 3603, 21, 3766, 3716, 3641, 3552, 3610, 
                     3590, 40, 3564, 3677, 3759, 3898, 3780, 3540, 3538, 3949, 3742, 3778, 3660, 38, 
                     3874, 3658, 3812, 3615, 3494, 3850, 3800, 3785, 3689, 3583, 3773, 3653, 3913, 3735, 
                     3608, 3672, 3627, 3684, 3711, 3843, 3588, 2067, 2079, 3010, 2276, 3503, 3522, 3527, 
                     3541, 3546, 3553, 3558, 3565, 2372, 3604, 3623, 3680, 3700, 3705, 3717, 3750, 3755, 
                     3762, 3793, 3938, 3940, 3801, 3820, 3844, 3849, 3851, 3856, 3863, 3870, 3894, 3731, 
                     3577, 3729, 3647, 3779, 3837, 3666, 3832, 3818, 3628, 3654, 3673, 3724, 3596, 3642, 
                     3774, 3539, 3661, 3630, 60, 34, 41, 3868, 3887, 3921, 3611, 3692, 3798, 3813, 3914, 
                     3685, 3806, 3736, 3635, 53, 3659, 3781, 3022, 3743, 3, 3882, 3875, 22, 58, 3616, 
                     3495, 39, 3899, 3748, 15, 27, 3584, 3609, 3560, 3678, 3697, 3825, 3767, 3902, 3591, 
                     3712, 3510, 3589, 3786, 3483, 3907, 2074, 3488, 3919, 3952, 10) 
    
    
    # download all data from repository 
    allTasks <- listOMLTasks() 
    
    # filter values that are in the study 
    tasksSel <- allTasks[allTasks$task.id %in% tasksInStudy, ]  
    
    return(tasksSel) 
} 
 
tasksSel <- loadTasks() 

tasks <-  subset(tasksSel, NumberOfFeatures > 2 &
                     NumberOfInstances < 100000  & NumberOfClasses == 2 &
                     NumberOfMissingValues == 0, select = task.id, drop = TRUE)  

tasks <- sample(tasks, 2) 

# convert OML tasks to mlr tasks 
size <- length(tasks) 
tasks.new <- c() 
for (i in 1:size ) {
    task <- getOMLTask(task.id = tasks[i]) 
    dname <- task$input$data.set$desc$name 
    task$input$evaluation.measures = measures 
    task <- convertOMLTaskToMlr(task)$mlr.task 
    
    # pack a task to a single list element that can be used later in a benchmark 
    tasks.new[i] <- list(task) 
} 

# lrns = list(makeLearner("classif.rpart"))  

learner = makeLearner("classif.ada", predict.type = "prob") 

rdesc = makeResampleDesc("CV", iters = 2L)
meas = list(auc, ber) 

# par.set <- makeParamSet(
#     makeIntegerParam("minsplit", 15, 300), 
#     makeIntegerParam("minbucket", 1, 300), 
#     makeNumericParam("cp", 0, 1) 
# ) 

par.set <- makeParamSet(
#     makeDiscreteParam(id = "loss", values = c("exponential", "logistic")),
#     makeDiscreteParam(id = "type", values = c("discrete", "real", "gentle")),
    makeIntegerParam(id = "iter", lower = 1L, upper = 100L),
    makeNumericParam(id = "nu", lower = 0, upper = 0.2),
    makeNumericParam(id = "bag.frac", lower = 0, upper = 1),
#     makeLogicalLearnerParam(id = "model.coef"), 
#     makeLogicalLearnerParam(id = "bag.shift"), 
    makeIntegerParam(id = "max.iter", lower = 1L, upper = 50L), 
    makeNumericParam(id = "delta", lower = 0, upper = 1e-9),
#     makeLogicalLearnerParam(id = "verbose", tunable = FALSE),
    makeIntegerParam(id = "minsplit", lower = 1L, upper = 50L), 
    # makeIntegerParam(id = "minbucket", lower = 1L, upper = 50L), 
    makeNumericParam(id = "cp", lower = 0, upper = 1),
#     makeIntegerParam(id = "maxcompete", lower = 0L, upper = 10L), 
#     makeIntegerParam(id = "maxsurrogate", lower = 0L, upper = 10L), 
#     makeDiscreteParam(id = "usesurrogate", values = 0:2),
#     makeDiscreteParam(id = "surrogatestyle", values = 0:1), 
    makeIntegerParam(id = "maxdepth", lower = 1L, upper = 30L) 
) 

MBOTuning = function(tasks.new, learner, par.set, budget = NULL) {
    
    mbo.control = makeMBOControl(propose.points = 5L) 
    mbo.control = setMBOControlInfill(mbo.control, crit = "cb") #confidence bound 

    obj.fun = makeSingleObjectiveFunction(
        name = "Tuned accuracy",
        fn = function(x){
            # print(x)
            new.learner = setHyperPars(learner, par.vals = as.list(x))
            
            bmr = benchmark(new.learner, tasks.new, rdesc, measures = meas) 

            # extract results 
            x <- bmr$results   

            # get value for each task  
            values <- lapply(seq_along(x),function(i) { 
                lapply(seq_along(x[[i]]), function(j) { 
                    ml.next <- x[[i]][[j]][[5]] 
                    value <- as.double(ml.next[1])         
                }) 
            }) 

            # aggregate results 
            values <- unlist(values) 
            mean_values <- mean(values) 
            
            return(mean_values)
        },
        par.set = par.set, 
        minimize = FALSE 
    )
    
    mbo.result = mbo(fun = obj.fun, control = mbo.control, show.info = TRUE)
    return(mbo.result)
    
} 
 
mbo.output = MBOTuning(tasks.new, learner = learner, par.set = par.set, budget = 2) 
print(mbo.output) 

plot(mbo.output) 


# lrns = list(makeLearner("classif.ada", predict.type = "prob")) 
# 
# tasks <-  subset(tasksSel, NumberOfFeatures > 2 &
#                      NumberOfInstances < 100000  & NumberOfClasses == 2 &
#                      NumberOfMissingValues == 0, select = task.id, drop = TRUE)  
# 
# tasks <- sample(tasks, 3) 
# meas = list(multiclass.auc, acc) 
# 
# size <- length(tasks) 
# 
# mlrTasks <- list() 
# 
# measures = c("predictive_accuracy", "build_cpu_time") 
# 
# for ( i in 1:size ) {
#     task <- getOMLTask(task.id = tasks[i]) 
#     dname <- task$input$data.set$desc$name 
#     task$input$evaluation.measures = measures 
#     task <- convertOMLTaskToMlr(task)  
#     mlrTasks[i] <- task 
# } 
# 
# rdesc = makeResampleDesc("CV", iters = 2L)
# 
# bmr = benchmark(lrns, mlrTasks, rdesc, measures = meas) 
# 
# x <- bmr$results  
# 
# values <- lapply(seq_along(x),function(i) { 
#     lapply(seq_along(x[[i]]), function(j) { 
#         ml.next <- x[[i]][[j]][[5]] 
#         value <- as.double(ml.next[1])         
#     }) 
# }) 
# 
# values <- unlist(values) 
# 
# mean_values <- mean(values) 


# change temp folder from default to a permanent lcoation to keep cache 
getOMLConfig()

setOMLConfig(cachedir = "mikhail/R/x86_64-pc-linux-gnu-library/tmp")  

# cache all information from OpenML  
populateOMLCache(dids = c(1:20)) 



