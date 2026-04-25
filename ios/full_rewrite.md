<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css">
<script defer src="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js"></script>
<script defer src="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/contrib/auto-render.min.js"
    onload="renderMathInElement(document.body, {delimiters: [{left: '75041', right: '75041', display: true}, {left: '$', right: '$', display: false}]});"></script>
<style>
    body { font-family: "Helvetica Neue", Helvetica, Arial, sans-serif; padding: 50px; line-height: 1.6; }
    h1, h2, h3 { color: #2c3e50; }
    .katex-display { margin: 1em 0; overflow-x: auto; overflow-y: hidden; }
</style>
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.css">
<script defer src="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/katex.min.js"></script>
<script defer src="https://cdn.jsdelivr.net/npm/katex@0.16.8/dist/contrib/auto-render.min.js" onload="renderMathInElement(document.body);"></script>
<style>
body { font-family: sans-serif; line-height: 1.5; padding: 40px; }
.katex { font-size: 1.1em; }
</style>
<style>
body {
    font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
    line-height: 1.6;
    color: #333;
    max-width: 800px;
    margin: 0 auto;
    padding: 2rem;
}
h1, h2, h3 {
    color: #111;
    margin-top: 2rem;
}
code {
    background: #f4f4f4;
    padding: 0.2rem 0.4rem;
    border-radius: 3px;
}
.math {
    font-size: 1.1em;
}
</style>
<script src="https://polyfill.io/v3/polyfill.min.js?features=es6"></script>
<script id="MathJax-script" async src="https://cdn.jsdelivr.net/npm/mathjax@3/es5/tex-mml-chtml.js"></script>
# Chapter 1: Overview

## 1.0 Chaos, Fractals, and Dynamics

There is a widespread fascination today with the concepts of chaos and fractals. Books on the subject have become bestsellers—an impressive achievement for mathematical and scientific topics. You can often find picture books showcasing the beauty of fractals on coffee tables everywhere, captivating even those without a mathematical background with their infinite, intricate patterns. 

Perhaps the most important aspect of chaos and fractals is that they represent mathematics that feels alive and constantly evolving. With a simple home computer, anyone can generate stunning, never-before-seen mathematical images. 

While the aesthetic appeal of these concepts explains their popularity, you might be feeling the urge to delve deeper. If you want to understand the mathematics behind the stunning pictures and see how these ideas apply to real-world problems in science and engineering, this text is for you.

The style here is informal and accessible. We will focus heavily on concrete examples and geometric intuition, rather than getting bogged down in abstract proofs. This is an extremely "applied" exploration—almost every concept is paired with an application from science or engineering, often drawn from modern research. Because no one is an expert in every field (like physics, biology, and fluid mechanics simultaneously), we will build the necessary scientific background from scratch. This approach is not only fun but highly instructive for seeing the underlying connections between disparate fields.

Before diving in, let's establish a foundational idea: chaos and fractals are sub-categories of an even broader field known as **dynamics**. Dynamics is the study of change—systems that evolve over time. Whether a system settles into a stable equilibrium, falls into repeating cycles, or exhibits highly complex, unpredictable behavior, we use the tools of dynamics to analyze it. You may have encountered dynamic concepts before in differential equations, classical mechanics, chemical kinetics, or population biology. When viewed through the lens of dynamics, all these subjects share a common, unifying framework.

Our deep dive into dynamics starts in Chapter 2. But first, we will look at two overviews of the subject: a historical perspective and a logical one. This chapter concludes with a "dynamical view of the world," which will serve as our guide for the rest of the material.

---

## 1.1 A Capsule History of Dynamics

Dynamics is highly interdisciplinary today, but its roots lie in physics. The subject was born in the mid-1600s when Isaac Newton invented calculus (specifically differential equations), formulated his laws of motion and universal gravitation, and used them to explain Kepler's laws of planetary motion. 

Newton successfully solved the **two-body problem**—calculating the Earth's orbit around the sun using the inverse-square law of gravitational attraction. Naturally, subsequent mathematicians and physicists tried to extend these methods to the **three-body problem** (e.g., modeling the sun, Earth, and moon together). Surprisingly, this proved to be vastly more difficult. After decades of struggle, mathematicians realized the three-body problem was fundamentally impossible to solve in the sense of finding a tidy, explicit formula for the exact motions of the bodies. The situation seemed completely hopeless.

A major breakthrough arrived in the late 1800s with the work of Henri Poincaré. He introduced a radical new perspective: instead of asking quantitative questions (like "What is the exact position of the planets at a specific time?"), he asked **qualitative** questions (like "Is the solar system forever stable, or will a planet eventually fly off into infinity?"). Poincaré developed a powerful geometric method to answer these kinds of questions. This geometric approach blossomed into modern dynamics, extending far beyond celestial mechanics. Poincaré was also the first to catch a glimpse of what we now call **chaos**—situations where deterministic, rule-based systems exhibit erratic behavior that depends so sensitively on initial conditions that long-term prediction becomes impossible.

During the first half of the twentieth century, chaos took a back seat as researchers focused on **nonlinear oscillators** and their applications in physics and engineering. Nonlinear oscillators were critical in developing technologies like radio, radar, lasers, and phase-locked loops. Theoretical work on these oscillators drove the invention of new mathematical techniques by pioneers like van der Pol, Andronov, Littlewood, Cartwright, Levinson, and Smale. Meanwhile, Poincaré’s geometric methods were expanded by Birkhoff, Kolmogorov, Arnol’d, and Moser to gain profound insights into classical mechanics.

The invention of the high-speed computer in the 1950s marked a critical turning point. Computers allowed scientists to experiment with equations in ways previously unimaginable, helping them build intuition for nonlinear systems. This experimentation led to meteorologist Edward Lorenz’s 1963 discovery of chaotic motion on a "strange attractor." While studying a simplified model of atmospheric convection to understand the unpredictability of weather, Lorenz found that his solutions never settled into an equilibrium or a repeating cycle. Instead, they oscillated in a completely irregular, aperiodic manner. 

Crucially, Lorenz noticed that if he started a simulation with two almost identical initial conditions, the resulting forecasts would quickly diverge and become entirely different. This implied that the system was inherently unpredictable—even microscopic errors in measuring the current state of the atmosphere would be rapidly amplified, leading to wildly inaccurate weather forecasts. Yet, Lorenz also discovered profound structure within the chaos. When plotted in three dimensions, the solutions formed a beautiful, butterfly-shaped set of points. We now recognize this set as a **fractal**.

Lorenz’s discoveries largely flew under the radar until the 1970s—the true boom years for chaos theory. Key developments during this decade included:
- **1971:** Ruelle and Takens proposed a new theory linking strange attractors to the onset of fluid turbulence.
- **Mid-1970s:** Robert May found chaos in simple iterated maps used in population biology, highlighting the dangers of relying solely on linear intuition.
- **Late 1970s:** Mitchell Feigenbaum discovered universal laws governing the transition from regular behavior to chaos, revealing that completely different systems become chaotic in the exact same mathematical way. This linked chaos to phase transitions and drew many physicists into the field.
- **Experimental Confirmations:** Scientists like Gollub, Libchaber, Swinney, Linsay, Moon, and Westervelt observed these chaotic behaviors in real-world experiments involving fluids, chemical reactions, electronic circuits, and semiconductors.

Two other major developments occurred in parallel during the 1970s. Benoit Mandelbrot popularized fractals, showing how these complex geometries applied to diverse natural phenomena. Simultaneously, in the emerging field of mathematical biology, Arthur Winfree applied geometric dynamics to biological rhythms, such as our 24-hour circadian clocks and heartbeats. 

By the 1980s, the study of dynamics, chaos, and fractals had exploded into a massive, interdisciplinary global effort.

---

## 1.2 The Importance of Being Nonlinear

Now let's move from history to the logical structure of dynamics. 

First, we distinguish between two main types of dynamical systems:
1. **Differential equations:** Used when time is continuous (flows continuously).
2. **Iterated maps (Difference equations):** Used when time is discrete (moves in distinct steps or ticks).

Differential equations are the most common tool in science and engineering, so we will focus primarily on them. Later, we'll see that iterated maps are fantastic tools for providing simple examples of chaos and analyzing the solutions of differential equations.

Within differential equations, we categorize them as:
- **Ordinary Differential Equations (ODEs):** Involve only one independent variable, time ( $ t $ ). Examples include the equations governing a pendulum or a swinging spring.
- **Partial Differential Equations (PDEs):** Involve both time ( $ t $ ) and space ( $ x, y, z $ ) as independent variables. An example is the heat equation, which tracks how temperature changes over time and across space.

In this text, we focus exclusively on temporal behavior—how things change over time—so we will deal almost entirely with **Ordinary Differential Equations**.

A general framework for representing ODEs looks like a system of equations:
 $  $  \dot{x}_1 = f_1(x_1, \dots, x_n)  $  $ 
 $  $  \vdots  $  $ 
 $  $  \dot{x}_n = f_n(x_1, \dots, x_n)  $  $ 

Here, the dot notation ( $ \dot{x} $ ) represents the derivative with respect to time ( $ dx/dt $ ). The variables  $ x_1, \dots, x_n $  represent the state of the system—these could be chemical concentrations, population sizes, or the velocities of planets. The functions  $ f_1, \dots, f_n $  define the specific rules governing how the system changes.

This setup helps us categorize systems as **linear** or **nonlinear**. A system is linear if all the  $ x_i $  variables on the right side of the equations appear to the first power only. Otherwise, the system is **nonlinear**. Nonlinear terms include things like multiplying variables together ( $ x_1 x_2 $ ), raising them to powers ( $ x_1^3 $ ), or placing them inside other functions ( $ \cos(x_2) $ ).

### An Example: The Pendulum
The swing of a simple pendulum is governed by the equation:
 $  $  \ddot{x} + \frac{g}{L} \sin(x) = 0  $  $ 
where  $ x $  is the angle,  $ g $  is gravity, and  $ L $  is the length of the pendulum. 

This equation is nonlinear because of the  $ \sin(x) $  term. Nonlinearity makes analytical solutions (exact formulas) exceedingly difficult to find. Historically, the standard "trick" to solve this was to "fudge" the math by using the small-angle approximation: assuming  $ x $  is very small,  $ \sin(x) \approx x $ . This transforms the equation into a linear one, which is easy to solve. However, this approximation forces us to throw away interesting physics, such as the pendulum having enough energy to whirl completely over the top. 

Are we forced to make these approximations? Not if we use geometric methods!

Instead of seeking a formula, we construct an abstract geometric space—a **phase space**. Suppose we track the pendulum's position  $ x_1 $  and its velocity  $ x_2 $ . We can plot the state of the pendulum as a single moving point  $ (x_1(t), x_2(t)) $  on a graph where the axes are position and velocity. 

The path this point traces as time passes is called a **trajectory**. The entire plane, filled with all possible trajectories from all possible starting conditions, is the **phase space**. 

Our goal in dynamics is to run this construction in reverse: given the governing equations, we want to sketch the trajectories in the phase space directly, extracting deep qualitative insights into the system's behavior *without ever solving the equations analytically*.

### Time-Dependent (Nonautonomous) Systems
What if an equation depends explicitly on time, like a forced oscillator where a pushing force changes over time? We can easily handle this by treating time  $ t $  as an extra variable in our system. For example, a system with two variables and a time-dependent force can be rewritten as a three-dimensional system where the third dimension is simply time flowing forward ( $ \dot{x}_3 = 1 $ ). This allows us to "freeze" the rules in an extra dimension, preserving our ability to use geometric phase space diagrams.

### Why Are Nonlinear Problems So Hard?
Linear systems are mathematically "friendly" because they can be broken down into individual parts. You can solve each part separately and simply add the answers together to get the complete solution. This is the **principle of superposition**. It says that the whole is exactly equal to the sum of its parts.

But the real world rarely acts this way! When parts of a system interact, cooperate, or compete, you have nonlinear interactions. If you listen to two of your favorite songs simultaneously, you do not experience double the pleasure—they interfere and create noise. Nonlinearity is the essential ingredient that makes lasers work, creates turbulence in fluids, and allows ecosystems to crash or thrive. 

---

## 1.3 A Dynamical View of the World

To map out our exploration of dynamics, we can organize all dynamical systems on a grid with two axes:
1. **The number of variables ( $ n $ )**: How many numbers are needed to describe the system's state? (This is the dimension of the phase space).
2. **Linear vs. Nonlinear**: Is the system linear (simple, superimposable) or nonlinear (complex, interacting)?

### The Grid of Dynamics
- ** $ n=1 $ , Linear:** The simplest systems. These govern basic exponential growth, radioactive decay, and simple electrical circuits. They only exhibit steady growth, decay, or stable equilibrium.
- ** $ n=2 $ , Linear:** By adding a second variable, systems gain the ability to oscillate. Examples include a simple mass on a spring or an oscillating electrical circuit.
- ** $ n \to \infty $ , Linear:** This is the realm of classical physics (Maxwell's equations for electromagnetism, heat diffusion, quantum mechanics). While massive, they are mathematically manageable because they are linear.
- ** $ n=1 $ , Nonlinear:** Moving to the nonlinear side, even single-variable systems become rich. We see concepts like fixed points, bifurcations, and basic population limits (like the logistic equation).
- ** $ n=2 $ , Nonlinear:** Here we encounter nonlinear oscillations, predator-prey cycles in biology, and the firing of individual neurons. 
- ** $ n \ge 3 $ , Nonlinear:** This is the domain of **chaos and fractals**. When a nonlinear system has at least three variables, it can exhibit strange attractors, wildly unpredictable weather patterns, and chaotic chemical kinetics.

The lower right corner of this grid—where systems are both massive (many variables) and nonlinear—represents **the frontier** of modern science. This region contains problems like fluid turbulence, immune system behavior, neural networks, and global economics. 

In this book, we will start in the simple lower-left corner ( $ n=1 $ , nonlinear) and methodically work our way to the right, increasing the dimensions and the complexity. As we move from simple fixed points ( $ n=1 $ ) to oscillations ( $ n=2 $ ) and finally to chaos ( $ n=3 $ ), our geometric approach will be our guiding light, illuminating the beautiful patterns hidden within complex systems.


# Chapter 2: Flows on the Line

## 2.0 Introduction

In Chapter 1, we introduced the general system of equations for dynamical systems:
 $  $  \dot{x}_1 = f_1(x_1, \dots, x_n)  $  $ 
 $  $  \vdots  $  $ 
 $  $  \dot{x}_n = f_n(x_1, \dots, x_n)  $  $ 

We mentioned that the solutions to these equations can be visualized as trajectories flowing through an  $ n $ -dimensional phase space. At this point, that concept probably sounds like a mind-bending abstraction. So, let’s start slowly and ground ourselves by looking at the simplest case:  $ n=1 $ . 

In an  $ n=1 $  system, we only have a single equation, which takes the form:
 $  $  \dot{x} = f(x)  $  $ 

Here,  $ x(t) $  is a real-valued function representing the state of the system over time  $ t $ , and  $ f(x) $  is a smooth mathematical function of  $ x $ . We call these equations **one-dimensional** or **first-order systems**.

Before moving forward, let's clarify two potential points of confusion regarding terminology:
1. **"System":** We are using the word "system" in the sense of a *dynamical system*, not in the classical algebra sense of a collection of two or more equations. Thus, a single differential equation can be a "system."
2. **Time Independence:** We do *not* allow  $ f $  to depend explicitly on time. Time-dependent (or "nonautonomous") equations like  $ \dot{x} = f(x, t) $  are more complicated because you need two pieces of information ( $ x $  and  $ t $ ) to predict the future. Therefore,  $ \dot{x} = f(x, t) $  is technically a two-dimensional (second-order) system and will be discussed later.

---

## 2.1 A Geometric Way of Thinking

When analyzing nonlinear systems, pictures are often far more helpful than complicated formulas. We will illustrate this with a simple example that introduces one of the most foundational techniques in dynamics: interpreting a differential equation as a **vector field**.

Consider the nonlinear differential equation:
 $  $  \dot{x} = \sin(x)  $  $ 

To highlight why pictures are better than formulas here, we purposefully chose one of the few nonlinear equations that *can* actually be solved exactly. Using separation of variables:
 $  $  \frac{dx}{\sin(x)} = dt  $  $ 
Integrating both sides gives:
 $  $  t = \int \csc(x) \,dx = -\ln|\csc(x) + \cot(x)| + C  $  $ 

If we assume the initial condition  $ x=x_0 $  at  $ t=0 $ , we can solve for  $ C $  to get the exact solution:
 $  $  t = \ln \left| \frac{\csc(x_0) + \cot(x_0)}{\csc(x) + \cot(x)} \right|  $  $ 

This formula is exact, but it is a massive headache to interpret! For example, if  $ x_0 = \pi/4 $ , can you easily describe what happens as  $ t \to \infty $ ? What if  $ x_0 $  is any arbitrary number? Staring at the formula won't give you an intuitive answer quickly.

In stark contrast, a graphical analysis of  $ \dot{x} = \sin(x) $  is clear and simple. 

We can think of  $ t $  as time,  $ x $  as the position of an imaginary particle moving along a line (the x-axis), and  $ \dot{x} $  as the velocity of that particle. The differential equation  $ \dot{x} = \sin(x) $  represents a **vector field** on the line: it dictates exactly how fast and in what direction the particle must move based on its current position  $ x $ .

To sketch the vector field:
1. Plot  $ \dot{x} $  versus  $ x $  (which is just graphing the sine wave).
2. Draw arrows on the x-axis to indicate velocity. If  $ \dot{x} > 0 $  (the sine wave is above the axis), the arrow points to the right. If  $ \dot{x} < 0 $  (the wave is below the axis), the arrow points to the left.

### The Flow of Phase Fluid
A more physical way to imagine this is that a fluid is flowing steadily along the x-axis, and its velocity varies from place to place according to the rule  $ \dot{x} = \sin(x) $ . 

At points where  $ \dot{x} = 0 $ , there is no flow. These are called **fixed points**. There are two kinds of fixed points:
- **Stable fixed points (attractors or sinks):** The flow on both sides points toward them. (Represented as solid black dots).
- **Unstable fixed points (repellers or sources):** The flow on both sides points away from them. (Represented as open circles).

Armed with this picture, solving the system visually is easy. We drop an imaginary particle at a starting position  $ x_0 $  and watch how the fluid carries it:
- If we start at  $ x_0 = \pi/4 $ , the particle moves right because  $ \sin(\pi/4) > 0 $ . It moves faster and faster until it hits  $ x = \pi/2 $  (where velocity is maximum). After that, it slows down and gradually approaches the stable fixed point at  $ x = \pi $ , but it takes infinite time to get there.
- The same logic applies to any initial condition. A particle simply moves in the direction of the arrows until it asymptotes toward the nearest stable fixed point. If it starts exactly on an unstable fixed point, it stays there forever (but the slightest nudge will send it flying away toward a stable one).

While pictures can't give us exact quantitative numbers (like the exact time maximum speed is reached), they immediately reveal the qualitative behavior, which is often exactly what we care about.

---

## 2.2 Fixed Points and Stability

The geometric ideas from the previous section can be applied to *any* one-dimensional system  $ \dot{x} = f(x) $ . You simply draw the graph of  $ f(x) $  and use it to sketch the vector field on the real line.

This imaginary line is called the **phase space**, and the imaginary fluid flowing along it is the **phase fluid**. To find the solution  $ x(t) $  starting from  $ x_0 $ , we place an imaginary **phase point** at  $ x_0 $  and watch it flow. The resulting function  $ x(t) $  is called the **trajectory**. A complete picture showing all the qualitatively different trajectories of a system is a **phase portrait**.

The behavior of the phase portrait is entirely controlled by the fixed points  $ x^* $ , defined mathematically as  $ f(x^*) = 0 $ . 

In terms of differential equations, fixed points represent **equilibrium solutions** (steady or constant solutions). If the system starts at  $ x^* $ , it stays there forever. 
- An equilibrium is **stable** if small disturbances away from it naturally dampen out and return to the equilibrium (stable fixed point).
- An equilibrium is **unstable** if small disturbances grow over time, pushing the system further away (unstable fixed point).

### Example 2.2.1
Find all fixed points for  $ \dot{x} = x^2 - 1 $  and classify their stability.
**Solution:** Set  $ \dot{x} = 0 $  to get  $ x^2 - 1 = 0 $ . The fixed points are  $ x^* = 1 $  and  $ x^* = -1 $ .
Graphing  $ f(x) = x^2 - 1 $  reveals a parabola opening upwards. For  $ x > 1 $  and  $ x < -1 $ , the parabola is positive (flow is right). Between  $ -1 < x < 1 $ , it is negative (flow is left). Therefore, the flow pushes towards  $ -1 $  (it is a stable fixed point) and pushes away from  $ 1 $  (it is an unstable fixed point). 
*Note on stability:* The point  $ -1 $  is "locally stable" because small disturbances decay. However, if a massive disturbance pushes the point past  $ +1 $ , the system will fly off to infinity rather than returning to  $ -1 $ . It is not "globally stable."

### Example 2.2.2: An Electrical Circuit
Consider an RC circuit (a resistor  $ R $  and a capacitor  $ C $  in series with a constant battery voltage  $ V_0 $ ). When the switch is closed, there is initially no charge on the capacitor. Let  $ Q(t) $  be the charge at time  $ t $ . Sketch  $ Q(t) $ .
**Solution:** Following Kirchhoff's voltage law around the loop, the voltage drop must be zero:
 $ -V_0 + IR + Q/C = 0 $ . 
Since the current  $ I $  is the rate of change of charge ( $ I = \dot{Q} $ ), we get:
 $ V_0 = R\dot{Q} + Q/C $ , which rearranges to  $ \dot{Q} = \frac{V_0}{R} - \frac{Q}{RC} $ .

This is our function  $ f(Q) $ . Graphing  $ \dot{Q} $  against  $ Q $  gives a straight line with a negative slope. The fixed point is where  $ \dot{Q} = 0 $ , which is  $ Q^* = CV_0 $ . Because the slope is negative, the flow is to the right when  $ Q < CV_0 $  and to the left when  $ Q > CV_0 $ . Thus,  $ Q^* = CV_0 $  is a globally stable fixed point. If we start at  $ Q=0 $ , the system flows smoothly and monotonically towards  $ CV_0 $ , slowing down as it approaches. 

### Example 2.2.3
Sketch the phase portrait for  $ \dot{x} = x - \cos(x) $  and determine stability.
**Solution:** We could graph  $ x - \cos(x) $  directly, but that’s tricky. Instead, note that fixed points occur where  $ x - \cos(x) = 0 $ , or where  $ x = \cos(x) $ . 
If we graph  $ y = x $  and  $ y = \cos(x) $  on the same axes, their intersection is the fixed point  $ x^* $ . When the line  $ y=x $  is above the cosine curve,  $ \dot{x} > 0 $  (flow right). When it is below,  $ \dot{x} < 0 $  (flow left). The intersection shows the flow pointing away in both directions. Therefore, there is exactly one fixed point, and it is strictly unstable—and we figured this out without ever needing a formula for  $ x^* $ !

---

## 2.3 Population Growth

The simplest mathematical model for a growing population is:
 $  $  \dot{N} = rN  $  $ 
where  $ N(t) $  is the population at time  $ t $ , and  $ r > 0 $  is the growth rate. This predicts basic, unconstrained exponential growth:  $ N(t) = N_0 e^{rt} $ .

In reality, exponential growth cannot continue forever. Overcrowding and limited resources eventually take their toll. To account for this, biologists assume that the *per capita growth rate* ( $ \dot{N}/N $ ) decreases when the population gets sufficiently large. 

For small  $ N $ , the growth rate is roughly  $ r $ . But for populations larger than a certain environmental **carrying capacity**  $ K $ , the growth rate becomes negative (the death rate exceeds the birth rate).

A mathematically elegant way to model this is to assume the per capita growth rate decreases linearly as  $ N $  increases. This gives us the famous **logistic equation** (first proposed by Verhulst in 1838 for human populations):
 $  $  \dot{N} = rN \left(1 - \frac{N}{K}\right)  $  $ 

While we could solve this analytically, the geometric approach is much more intuitive. We plot  $ \dot{N} $  versus  $ N $  (only for  $ N \ge 0 $ , since negative population doesn't make sense). This results in a downward-opening parabola passing through the origin.

Setting  $ \dot{N} = 0 $  gives two fixed points:  $ N^* = 0 $  and  $ N^* = K $ .
Looking at the flow on the parabola:
- Between  $ 0 $  and  $ K $ ,  $ \dot{N} $  is positive, so the population grows.
- Above  $ K $ ,  $ \dot{N} $  is negative, so the population shrinks.

This means  $ N^* = 0 $  is an unstable fixed point (a tiny population will explode exponentially), and  $ N^* = K $  is a stable fixed point (the population always settles at the carrying capacity). 

If we start with  $ N_0 < K/2 $ , the population grows increasingly fast (accelerating) until it hits the peak of the parabola at  $ K/2 $ . After that, growth slows down (decelerating) until it asymptotes at  $ K $ . This creates a classic S-shaped (sigmoid) growth curve. If we start with  $ N_0 > K $ , the population simply decays smoothly down to  $ K $ .

### Critique of the Logistic Model
We shouldn't take the algebraic form of the logistic model too literally; it is essentially a mathematical metaphor for populations that grow from zero to a carrying capacity. It works beautifully for simple laboratory colonies of yeast or bacteria grown in constant conditions. However, it fails for complex organisms (like fruit flies or beetles) that have complex life cycles (eggs, larvae, adults) or experience time-delayed effects from overcrowding, which often lead to persistent fluctuations rather than a smooth approach to a steady limit.

---

## 2.4 Linear Stability Analysis

While graphical methods are fantastic for determining stability, we often want a quantitative measure, such as exactly *how fast* a system decays to a stable fixed point. We can find this using **linear stability analysis**, which involves linearizing the system around the fixed point.

Let  $ x^* $  be a fixed point, and let  $ \eta(t) = x(t) - x^* $  represent a very small perturbation (or disturbance) away from the fixed point. We want to know if  $ \eta(t) $  grows or shrinks.

Taking the derivative with respect to time:
 $  $  \dot{\eta} = \dot{x}  $  $ 
(Since  $ x^* $  is a constant). We know  $ \dot{x} = f(x) $ , so:
 $  $  \dot{\eta} = f(x^* + \eta)  $  $ 

Because  $ \eta $  is very small, we can use a Taylor series expansion:
 $  $  f(x^* + \eta) = f(x^*) + \eta f'(x^*) + O(\eta^2)  $  $ 
Where  $ O(\eta^2) $  represents quadratically small, negligible terms. Because  $ x^* $  is a fixed point,  $ f(x^*) = 0 $ . This leaves us with:
 $  $  \dot{\eta} \approx \eta f'(x^*)  $  $ 

This is a linear differential equation for the perturbation  $ \eta $ . The term  $ f'(x^*) $  is just a constant (it is the slope of the  $ f(x) $  graph at the fixed point). 

This equation shows that the perturbation grows exponentially if  $ f'(x^*) > 0 $  and decays exponentially if  $ f'(x*) < 0 $ . If  $ f'(x^*) = 0 $ , the  $ O(\eta^2) $  terms are no longer negligible, and this linear analysis fails—we must return to graphical methods.

**The Takeaway:** The slope  $ f'(x^*) $  at the fixed point determines stability. Its sign tells us if it is stable or unstable, and its magnitude  $ |f'(x^*)| $  acts as the exponential decay/growth rate. The reciprocal  $ 1/|f'(x^*)| $  is the **characteristic time scale**, representing the time required for the system to undergo significant change near the fixed point.

### Example 2.4.1
Classify the stability of the fixed points for  $ \dot{x} = \sin(x) $  using linear stability analysis.
**Solution:** The fixed points are  $ x^* = k\pi $ , where  $ k $  is an integer. The derivative is  $ f'(x) = \cos(x) $ . 
Evaluating the derivative at the fixed points:  $ f'(k\pi) = \cos(k\pi) $ . 
This equals  $ 1 $  if  $ k $  is even, and  $ -1 $  if  $ k $  is odd. Therefore, the fixed points are unstable for even multiples of  $ \pi $  and stable for odd multiples, which perfectly matches our graphical analysis.

### Example 2.4.3: When Linear Analysis Fails
What happens if  $ f'(x^*) = 0 $ ? 
**Solution:** The linear approximation fails, and anything can happen. We must check graphically. For example, consider:
- (a)  $ \dot{x} = -x^3 $  (Stable at  $ x=0 $ )
- (b)  $ \dot{x} = x^3 $  (Unstable at  $ x=0 $ )
- (c)  $ \dot{x} = x^2 $  (Half-stable at  $ x=0 $ : attracts from the left, repels to the right)
- (d)  $ \dot{x} = 0 $  (A whole line of fixed points; neutrally stable)
All of these have  $ f'(0) = 0 $ , but their stabilities are completely different. Such cases are critically important when we study **bifurcations** later on.


## 2.5 Existence and Uniqueness

Up to this point, our treatment of vector fields has been delightfully informal. We haven't worried about whether solutions to  $ \dot{x} = f(x) $  actually exist, or if they do, whether they are unique. This casual attitude is in keeping with the "applied" spirit of this exploration. However, we need to be aware of what can go wrong in pathological cases so we aren't blindsided.

### Example 2.5.1: Non-Uniqueness
Show that the solution to  $ \dot{x} = x^{1/3} $  starting from  $ x_0 = 0 $  is not unique.
**Solution:** Since  $ x=0 $  makes  $ \dot{x} = 0 $ ,  $ x=0 $  is a fixed point. One obvious solution is that the system just sits there forever:  $ x(t) = 0 $  for all  $ t $ . 
But surprisingly, there is another solution! If we separate variables and integrate:
 $  $  \int x^{-1/3} \,dx = \int dt  $  $ 
 $  $  \frac{3}{2} x^{2/3} = t + C  $  $ 
Since  $ x(0) = 0 $ , the constant  $ C=0 $ . Solving for  $ x $  yields:
 $  $  x(t) = \left(\frac{2}{3} t\right)^{3/2}  $  $ 
This is *also* a perfectly valid solution! In fact, there are infinitely many solutions. The system could sit at  $ x=0 $  for an arbitrary amount of time, and then suddenly "take off" following this curve. 

When uniqueness fails, our entire geometric approach collapses. If we place a phase point at the origin, the phase fluid doesn't know what to do with it! Should it stay, or should it move? 
The source of this pathology becomes clear if we look at the derivative:  $ f'(0) = \infty $ . The slope is infinitely steep at the origin, meaning the fixed point is violently unstable.

To protect ourselves from these headaches, mathematicians have proven a theorem:

**Existence and Uniqueness Theorem:**
Consider the initial value problem:  $ \dot{x} = f(x) $  with  $ x(0) = x_0 $ .
If both  $ f(x) $  and its derivative  $ f'(x) $  are continuous on an open interval around  $ x_0 $ , then the problem has a solution  $ x(t) $  for some time interval around  $ t=0 $ , and that solution is **unique**.

This theorem essentially says that as long as our vector field is "smooth enough," solutions exist and are unique. However, it *doesn't* guarantee the solutions will exist forever!

### Example 2.5.2: Finite-Time Blow-Up
Consider  $ \dot{x} = 1 + x^2 $  with  $ x(0) = 0 $ . Do solutions exist for all time?
**Solution:** Here,  $ f(x) = 1 + x^2 $ . This function and its derivative are beautifully continuous everywhere. The theorem guarantees a unique solution. 
Separating variables and integrating:
 $  $  \int \frac{dx}{1+x^2} = \int dt  $  $ 
 $  $  \arctan(x) = t + C  $  $ 
Using  $ x(0) = 0 $ , we get  $ C=0 $ , so the solution is  $ x(t) = \tan(t) $ . 
Notice what happens as  $ t $  approaches  $ \pi/2 $ :  $ \tan(t) $  shoots off to infinity! The solution only exists for  $ -\pi/2 < t < \pi/2 $ . Outside of that narrow time window, the solution ceases to exist. 

This phenomenon of reaching infinity in a finite amount of time is called **blow-up**. It has real physical relevance in models of runaway processes, like combustion or explosions. 

From here on out, we generally won't worry about existence and uniqueness. Our vector fields will typically be smooth enough to behave well. If we hit a dangerous example, we'll deal with it then.

---

## 2.6 Impossibility of Oscillations

Fixed points absolutely dominate the dynamics of first-order systems. In fact, for a vector field on the real line, only two things can ever happen:
1. Trajectories approach a fixed point.
2. Trajectories diverge off to infinity.

Why is this? Because trajectories are forced to move strictly in one direction (monotonically) or remain totally stationary. Geometrically, a phase point on a line can never reverse its direction.

Because a phase point can't turn around, **overshoot and damped oscillations can never occur in a first-order system.** For the exact same reason, continuous, undamped oscillations are completely impossible. **There are no periodic solutions to  $ \dot{x} = f(x) $ .**

These results are fundamentally topological. They arise simply because we are flowing along a 1D line. If you flow strictly in one direction on a line, you can never return to where you started. (If we were flowing on a *circle* instead of a line, we *could* return to the start—which is why periodic solutions *are* possible for vector fields on a circle, a topic for Chapter 4).

### A Mechanical Analog: Overdamped Systems
It might seem surprising that  $ \dot{x} = f(x) $  can never oscillate. But it makes perfect physical sense if we view  $ \dot{x} = f(x) $  as an extreme limiting case of Newton's law:  $ m\ddot{x} = F $ .

Imagine a mass  $ m $  attached to a nonlinear spring with restoring force  $ F(x) $ . Now, imagine plunging this entire system into a vat of incredibly thick, viscous fluid—like thick honey or molasses. The mass is now subject to massive viscous damping,  $ -b\dot{x} $ . 

Newton's law becomes:
 $  $  m\ddot{x} + b\dot{x} = F(x)  $  $ 

If the viscous damping is overwhelmingly strong compared to the mass's inertia ( $ b\dot{x} \gg m\ddot{x} $ ), the inertia term becomes negligible. The system essentially behaves like  $ b\dot{x} = F(x) $ , or:
 $  $  \dot{x} = \frac{1}{b} F(x) = f(x)  $  $ 

This is the **overdamped limit**. If you pull the mass through the honey and let go, the spring slowly and agonizingly drags it back to equilibrium. The honey is so thick that the mass will never overshoot the equilibrium point, and it certainly won't bounce back and forth! This physical intuition perfectly matches our geometric proof that first-order systems cannot oscillate.

---

## 2.7 Potentials

There is another powerful way to visualize  $ \dot{x} = f(x) $ , using the physics concept of **potential energy**. We can picture our system as a particle sliding down the walls of a potential energy well.

We define a potential function  $ V(x) $  such that:
 $  $  f(x) = -\frac{dV}{dx}  $  $ 

As with the honey example above, imagine the particle is heavily damped. The negative sign is a standard physics convention ensuring the particle always moves "downhill" toward lower potential energy.

To prove this mathematically, we calculate how the potential energy changes over time as the particle moves:
 $  $  \frac{dV}{dt} = \frac{dV}{dx} \cdot \frac{dx}{dt}  $  $ 
Since  $ \frac{dx}{dt} = \dot{x} = f(x) = -\frac{dV}{dx} $ , we can substitute this in:
 $  $  \frac{dV}{dt} = \frac{dV}{dx} \cdot \left(-\frac{dV}{dx}\right) = -\left(\frac{dV}{dx}\right)^2 \le 0  $  $ 

Because this value is always negative (or zero),  $ V(t) $  must always decrease along trajectories. The particle always moves downhill. 
If the particle reaches an equilibrium point where  $ \frac{dV}{dx} = 0 $ , then  $ \frac{dV}{dt} = 0 $ , and the potential remains constant. 
- Local **minima** of  $ V(x) $  (the bottom of the valleys) correspond to **stable fixed points**.
- Local **maxima** of  $ V(x) $  (the peaks of the hills) correspond to **unstable fixed points**.

### Example 2.7.2: A Bistable System
Graph the potential for  $ \dot{x} = x - x^3 $  and identify the equilibria.
**Solution:** We set  $ -\frac{dV}{dx} = x - x^3 $ . Integrating gives:
 $  $  V(x) = -\frac{1}{2}x^2 + \frac{1}{4}x^4 + C  $  $ 
(We usually set the arbitrary constant  $ C=0 $  for convenience).
Graphing this potential reveals a "W" shape: a hill in the middle at  $ x=0 $ , flanked by two valleys at  $ x=1 $  and  $ x=-1 $ . 
The local minima at  $ x=\pm1 $  are stable equilibria. The local maximum at  $ x=0 $  is an unstable equilibrium. Because this system has two distinct stable states (two valleys), it is called a **double-well potential** and the system is said to be **bistable**.

---

## 2.8 Solving Equations on the Computer

Throughout this chapter, we have used geometric and analytical tools. A modern dynamicist must also master a third tool: numerical methods on a computer. Computers allow us to approximate solutions to analytically impossible problems and beautifully visualize the results.

The core problem of numerical integration is this: Given  $ \dot{x} = f(x) $  and a starting point  $ x_0 $  at  $ t_0 $ , how do we systematically calculate  $ x(t) $ ?

### Euler's Method
Imagine we are riding on our phase fluid. At  $ x_0 $ , our velocity is  $ f(x_0) $ . If we flow forward for a tiny time step  $ \Delta t $ , distance equals rate times time, so we move by an amount  $ f(x_0)\Delta t $ .
Our new position  $ x_1 $  is approximately:
 $  $  x_1 = x_0 + f(x_0)\Delta t  $  $ 

Now we repeat this process from our new location  $ x_1 $ , calculating our new velocity  $ f(x_1) $ , and stepping forward again:
 $  $  x_{n+1} = x_n + f(x_n)\Delta t  $  $ 

This is **Euler's Method**, the simplest numerical integration scheme. However, because our velocity is actually constantly changing during the step  $ \Delta t $ , assuming it is constant introduces errors. Unless  $ \Delta t $  is incredibly tiny, Euler's method gets inaccurate very quickly. It is rarely used in practice, but it contains the conceptual core of all numerical methods.

### Refinements
The flaw in Euler's method is that it only looks at the velocity at the *start* of the time step. The **Improved Euler Method** takes a "trial step" to the end of the interval, calculates the velocity there, and then averages the starting and ending velocities to make the "real step." This significantly reduces the error.

Even better is the **Runge-Kutta method**, the workhorse of everyday numerical integration. It essentially samples the velocity at four carefully chosen points during the time step and takes a weighted average. This 4th-order method provides excellent accuracy without requiring excessively tiny time steps. 

### Why Not Always Use a Tiny  $ \Delta t $ ?
You might wonder why we don't just use a microscopically small  $ \Delta t $  with a simple method. The problem is **round-off error**. Computers do not have infinite decimal precision. Every single calculation chops off a tiny fraction of the true value. If you take millions of tiny steps, these microscopic round-off errors accumulate into massive, catastrophic errors, destroying your solution. Therefore, finding a balance between step size and algorithm accuracy is crucial.

### Visualizing Numerical Solutions: The Slope Field
For the equation  $ \dot{x} = f(x) $ , we can plot the **slope field** in the  $ (t, x) $  plane. For every coordinate point  $ (t, x) $ , the equation tells us the exact slope ( $ dx/dt $ ) a trajectory would have passing through that point. We can draw tiny line segments representing these slopes on a grid. 
Solving the equation numerically is akin to drawing a smooth curve that flows seamlessly through this field, always staying perfectly tangent to the local slope lines.

Computers are indispensable for studying dynamical systems, and they will be used liberally throughout the rest of our exploration.


# Exercises for Chapter 2

## 2.1 A Geometric Way of Thinking

**In the next three exercises, interpret  $ \dot{x} = \sin(x) $  as a flow on the line.**

**2.1.1** Find all the fixed points of the flow.

**2.1.2** At which points  $ x $  does the flow have the greatest velocity to the right?

**2.1.3** 
a) Find the flow’s acceleration  $ \ddot{x} $  as a function of  $ x $ .
b) Find the points where the flow has maximum positive acceleration.

**2.1.4 (Exact solution of  $ \dot{x} = \sin(x) $ )** 
As shown in the text,  $ \dot{x} = \sin(x) $  has the solution  $ t = \ln \left| \frac{\csc(x_0) + \cot(x_0)}{\csc(x) + \cot(x)} \right| $ , where  $ x_0 = x(0) $  is the initial value of  $ x $ .
a) Given the specific initial condition  $ x_0 = \pi/4 $ , show that the solution above can be inverted to obtain  $ x(t) = 2 \arctan\left(\frac{e^t}{1 + \sqrt{2}}\right) $ . Conclude that  $ x(t) \to \pi $  as  $ t \to \infty $ , as claimed in Section 2.1. *(Hint: You need to be good with trigonometric identities to solve this problem.)*
b) Try to find the analytical solution for  $ x(t) $ , given an arbitrary initial condition  $ x_0 $ .

**2.1.5 (A mechanical analog)**
a) Find a mechanical system that is approximately governed by  $ \dot{x} = \sin(x) $ .
b) Using your physical intuition, explain why it now becomes obvious that  $ x^* = 0 $  is an unstable fixed point and  $ x^* = \pi $  is stable.

---

## 2.2 Fixed Points and Stability

**Analyze the following equations graphically. In each case, sketch the vector field on the real line, find all the fixed points, classify their stability, and sketch the graph of  $ x(t) $  for different initial conditions. Then try for a few minutes to obtain the analytical solution for  $ x(t) $ ; if you get stuck, don’t try for too long since in several cases it’s impossible to solve the equation in closed form!**

**2.2.1**  $ \dot{x} = 4x^2 - 16 $ 
**2.2.2**  $ \dot{x} = 1 - x^{14} $ 
**2.2.3**  $ \dot{x} = x - x^3 $ 
**2.2.4**  $ \dot{x} = e^{-x} \sin(x) $ 
**2.2.5**  $ \dot{x} = 1 + \frac{1}{2} \cos(x) $ 
**2.2.6**  $ \dot{x} = 1 - 2\cos(x) $ 
**2.2.7**  $ \dot{x} = e^x - \cos(x) $  *(Hint: Sketch the graphs of  $ e^x $  and  $ \cos(x) $  on the same axes, and look for intersections. You won’t be able to find the fixed points explicitly, but you can still find the qualitative behavior.)*

**2.2.8 (Working backwards, from flows to equations)** 
Given an equation  $ \dot{x} = f(x) $ , we know how to sketch the corresponding flow on the real line. Here you are asked to solve the opposite problem: For a phase portrait that has unstable fixed points at  $ x=-1 $  and  $ x=2 $ , and a stable fixed point at  $ x=0 $ , find an equation that is consistent with it. *(There are an infinite number of correct answers—and wrong ones too.)*

**2.2.9 (Backwards again, now from solutions to equations)** 
Find an equation  $ \dot{x} = f(x) $  whose solutions  $ x(t) $  monotonically approach  $ x=1 $  from above, and monotonically approach  $ x=-1 $  from below, with an unstable fixed point at  $ x=0 $ .

**2.2.10 (Fixed points)** 
For each of (a)–(e), find an equation  $ \dot{x} = f(x) $  with the stated properties, or if there are no examples, explain why not. (In all cases, assume that  $ f(x) $  is a smooth function.)
a) Every real number is a fixed point.
b) Every integer is a fixed point, and there are no others.
c) There are precisely three fixed points, and all of them are stable.
d) There are no fixed points.
e) There are precisely 100 fixed points.

**2.2.11 (Analytical solution for charging capacitor)** 
Obtain the analytical solution of the initial value problem  $ \dot{Q} = \frac{V_0}{R} - \frac{Q}{RC} $ , with  $ Q(0) = 0 $ , which arose in Example 2.2.2.

**2.2.12 (A nonlinear resistor)** 
Suppose the resistor in Example 2.2.2 is replaced by a nonlinear resistor. In other words, this resistor does not have a linear relation between voltage and current. Such nonlinearity arises in certain solid-state devices. Instead of  $ IR = V_0 - Q/C $ , suppose we have  $ g(I) = V_0 - Q/C $ , where  $ g(I) $  is an S-shaped nonlinear function. Redo Example 2.2.2 in this case. Derive the circuit equations, find all the fixed points, and analyze their stability. What qualitative effects does the nonlinearity introduce (if any)?

**2.2.13 (Terminal velocity)** 
The velocity  $ v(t) $  of a skydiver falling to the ground is governed by  $ m\dot{v} = mg - kv^2 $ , where  $ m $  is the mass of the skydiver,  $ g $  is the acceleration due to gravity, and  $ k > 0 $  is a constant related to the amount of air resistance.
a) Obtain the analytical solution for  $ v(t) $ , assuming that  $ v(0) = 0 $ .
b) Find the limit of  $ v(t) $  as  $ t \to \infty $ . This limiting velocity is called the **terminal velocity**.
c) Give a graphical analysis of this problem, and thereby re-derive a formula for the terminal velocity.
d) An experimental study (Carlson et al. 1942) confirmed that the equation gives a good quantitative fit to data on human skydivers. Six men were dropped from altitudes varying from 10,600 feet to 31,400 feet to a terminal altitude of 2,100 feet, at which they opened their parachutes. The long free fall from 31,400 to 2,100 feet took 116 seconds. The average weight of the men and their equipment was 261.2 pounds. In these units,  $ g = 32.2 \, \text{ft/sec}^2 $ . Compute the average velocity  $ V_{avg} $ .
e) Using the data given here, estimate the terminal velocity, and the value of the drag constant  $ k $ . *(Hints: First you need to find an exact formula for  $ s(t) $ , the distance fallen, where  $ s(0) = 0 $ ,  $ \dot{s} = v $ , and  $ v(t) $  is known from part (a). Then solve for  $ V $  graphically or numerically.)*

---

## 2.3 Population Growth

**2.3.1 (Exact solution of logistic equation)** 
There are two ways to solve the logistic equation  $ \dot{N} = rN\left(1 - \frac{N}{K}\right) $  analytically for an arbitrary initial condition  $ N_0 $ .
a) Separate variables and integrate, using partial fractions.
b) Make the change of variables  $ x = 1/N $ . Then derive and solve the resulting differential equation for  $ x $ .

**2.3.2 (Autocatalysis)** 
Consider the model chemical reaction  $ A + X \rightleftharpoons 2X $  with forward rate  $ k_1 $  and backward rate  $ k_{-1} $ . This means that the chemical  $ X $  stimulates its own production, a process called **autocatalysis**. Assuming there's an enormous surplus of chemical  $ A $ , so its concentration  $ a $  is constant, the equation for the kinetics of  $ x $  (concentration of  $ X $ ) is:
 $  $  \dot{x} = k_1 a x - k_{-1} x^2  $  $ 
a) Find all the fixed points of this equation and classify their stability.
b) Sketch the graph of  $ x(t) $  for various initial values  $ x_0 $ .

**2.3.3 (Tumor growth)** 
The growth of cancerous tumors can be modeled by the Gompertz law  $ \dot{N} = -aN \ln(bN) $ , where  $ N(t) $  is proportional to the number of cells in the tumor, and  $ a, b > 0 $  are parameters.
a) Interpret  $ a $  and  $ b $  biologically.
b) Sketch the vector field and then graph  $ N(t) $  for various initial values.

**2.3.4 (The Allee effect)** 
For certain species, the effective growth rate  $ \dot{N}/N $  is highest at intermediate  $ N $ . This is called the **Allee effect**. For example, it is too hard to find mates when  $ N $  is very small, and there is too much competition when  $ N $  is large.
a) Show that  $ \dot{N}/N = r - a(N - b)^2 $  provides an example of the Allee effect, if  $ r, a, $  and  $ b $  satisfy certain constraints (to be determined).
b) Find all the fixed points of the system and classify their stability.
c) Sketch the solutions  $ N(t) $  for different initial conditions.
d) Compare the solutions  $ N(t) $  to those found for the logistic equation. What are the qualitative differences, if any?

**2.3.5 (Dominance of the fittest)** 
Suppose  $ X $  and  $ Y $  are two species that reproduce exponentially fast:  $ \dot{X} = aX $  and  $ \dot{Y} = bY $ , respectively, with  $ X(0) > 0, Y(0) > 0 $ , and growth rates  $ a > b > 0 $ . Here  $ X $  is "fitter" than  $ Y $ . We expect  $ X $  to keep increasing its share of the total population  $ X+Y $  as  $ t \to \infty $ .
a) Let  $ x(t) = X(t) / [X(t) + Y(t)] $  denote  $ X $ 's share of the population. By solving for  $ X(t) $  and  $ Y(t) $ , show that  $ x(t) $  increases monotonically and approaches  $ 1 $  as  $ t \to \infty $ .
b) Alternatively, derive a differential equation for  $ x(t) $ . Show that  $ x(t) $  obeys the logistic equation  $ \dot{x} = (a-b)x(1-x) $ . Explain why this implies  $ x(t) $  approaches  $ 1 $ .

**2.3.6 (Language death)** 
Abrams and Strogatz (2003) proposed a model of language competition. Let  $ x $  be the proportion of the population speaking language  $ X $ , and  $ 1-x $  the proportion speaking  $ Y $ . The system evolves according to:
 $  $  \dot{x} = (1-x)x^a s - x(1-x)^a (1-s)  $  $ 
where  $ 0 < s < 1 $  reflects the social status of language  $ X $ , and  $ a > 1 $  is an adjustable parameter.
a) Show that this equation for  $ x $  has three fixed points.
b) Show that for all  $ a > 1 $ , the fixed points at  $ x=0 $  and  $ x=1 $  are both stable.
c) Show that the third fixed point,  $ 0 < x^* < 1 $ , is unstable.
*(This model therefore predicts that two languages cannot coexist stably—one will eventually drive the other to extinction.)*

## 2.4 Linear Stability Analysis

**Use linear stability analysis to classify the fixed points of the following systems. If linear stability analysis fails because  $ f'(x^*) = 0 $ , use a graphical argument to decide the stability.**

**2.4.1**  $ \dot{x} = x(1-x) $ 
**2.4.2**  $ \dot{x} = x(1-x)(2-x) $ 
**2.4.3**  $ \dot{x} = \tan(x) $ 
**2.4.4**  $ \dot{x} = x^2(6-x) $ 
**2.4.5**  $ \dot{x} = 1 - e^{-x^2} $ 
**2.4.6**  $ \dot{x} = \ln(x) $ 
**2.4.7**  $ \dot{x} = ax - x^3 $ , where  $ a $  can be positive, negative, or zero. Discuss all three cases.

**2.4.8** 
Using linear stability analysis, classify the fixed points of the Gompertz model of tumor growth  $ \dot{N} = -aN \ln(bN) $ .

**2.4.9 (Critical slowing down)** 
In statistical mechanics, "critical slowing down" is a signature of a second-order phase transition. The system relaxes to equilibrium much more slowly than usual. 
a) Obtain the analytical solution to  $ \dot{x} = -x^3 $  for an arbitrary initial condition. Show that  $ x(t) \to 0 $  as  $ t \to \infty $  but that the decay is not exponential. *(You should find that the decay is a much slower algebraic function of  $ t $ .)*
b) To get some intuition about the slowness, sketch a numerically accurate plot of the solution for  $ x_0 = 10 $ , for  $ 0 \le t \le 10 $ . On the same graph, plot the solution to  $ \dot{x} = -x $  for the same initial condition.

---

## 2.5 Existence and Uniqueness

**2.5.1 (Reaching a fixed point in a finite time)** 
A particle travels on the half-line  $ x \ge 0 $  with a velocity given by  $ \dot{x} = -xc $ , where  $ c $  is a real constant.
a) Find all values of  $ c $  such that the origin  $ x=0 $  is a stable fixed point.
b) Now assume that  $ c $  is chosen such that  $ x=0 $  is stable. Can the particle ever reach the origin in a finite time? Specifically, how long does it take for the particle to travel from  $ x=1 $  to  $ x=0 $ , as a function of  $ c $ ?

**2.5.2 ("Blow-up": Reaching infinity in a finite time)** 
Show that the solution to  $ \dot{x} = 1 + x^{10} $  escapes to infinity in a finite time, starting from any initial condition. *(Hint: Don’t try to find an exact solution; instead, compare the solutions to those of  $ \dot{x} = 1 + x^2 $ .)*

**2.5.3** 
Consider the equation  $ \dot{x} = rx + x^3 $ , where  $ r > 0 $  is fixed. Show that  $ x(t) \to \infty $  in finite time, starting from any initial condition  $ x_0 > 0 $ .

**2.5.4 (Infinitely many solutions with the same initial condition)** 
Show that the initial value problem  $ \dot{x} = x^{1/3}, \, x(0) = 0 $  has an infinite number of solutions. *(Hint: Construct a solution that stays at  $ x=0 $  until some arbitrary time  $ t_0 $ , after which it takes off.)*

**2.5.5 (A general example of non-uniqueness)** 
Consider the initial value problem  $ \dot{x} = |x|^{p/q}, \, x(0) = 0 $  where  $ p $  and  $ q $  are positive integers with no common factors.
a) Show that there are an infinite number of solutions if  $ p < q $ .
b) Show that there is a unique solution if  $ p \ge q $ .

**2.5.6 (The leaky bucket)** 
Consider a water bucket with a hole in the bottom. If you see an empty bucket with a puddle beneath it, you cannot figure out when the bucket was full. The solution to the corresponding differential equation must be non-unique when integrated backwards in time.
Let  $ h(t) = $  height of water remaining;  $ a = $  area of the hole;  $ A = $  cross-sectional area of bucket;  $ v(t) = $  velocity of water escaping.
a) Show that  $ av(t) = -A\dot{h}(t) $ . What physical law are you invoking?
b) Using conservation of energy, derive  $ v^2 = 2gh $ .
c) Combining (a) and (b), show  $ \dot{h} = -C\sqrt{h} $ , where  $ C = \frac{a}{A}\sqrt{2g} $ .
d) Given  $ h(0) = 0 $  (bucket empty at  $ t=0 $ ), show that the solution for  $ h(t) $  is non-unique in backwards time ( $ t < 0 $ ).

---

## 2.6 Impossibility of Oscillations

**2.6.1** 
Explain this paradox: a simple harmonic oscillator  $ m\ddot{x} = -kx $  is a system that oscillates in one dimension (along the x-axis). But the text says one-dimensional systems can’t oscillate.

**2.6.2 (No periodic solutions to  $ \dot{x} = f(x) $ )** 
Here’s an analytic proof that periodic solutions are impossible for a vector field on a line. Suppose on the contrary that  $ x(t) $  is a nontrivial periodic solution, i.e.,  $ x(t) = x(t + T) $  for some  $ T > 0 $ , and  $ x(t) \neq x(t + s) $  for all  $ 0 < s < T $ . Derive a contradiction by considering the integral  $ \int_t^{t+T} f(x) \frac{dx}{dt} dt $ .

---

## 2.7 Potentials

**For each of the following vector fields, plot the potential function  $ V(x) $  and identify all the equilibrium points and their stability.**

**2.7.1**  $ \dot{x} = x(1-x) $ 
**2.7.2**  $ \dot{x} = 3 $ 
**2.7.3**  $ \dot{x} = \sin(x) $ 
**2.7.4**  $ \dot{x} = 2 + \sin(x) $ 
**2.7.5**  $ \dot{x} = -\sinh(x) $ 
**2.7.6**  $ \dot{x} = r + x - x^3 $ , for various values of  $ r $ .

**2.7.7 (Another proof that solutions to  $ \dot{x} = f(x) $  can’t oscillate)** 
Let  $ \dot{x} = f(x) $  be a vector field on the line. Use the existence of a potential function  $ V(x) $  to show that solutions  $ x(t) $  cannot oscillate.

---

## 2.8 Solving Equations on the Computer

**2.8.1 (Slope field)** 
The slope is constant along horizontal lines in a slope field for autonomous equations. Why should we have expected this?

**2.8.2** 
Sketch the slope field for the following differential equations. Then "integrate" the equation manually by drawing trajectories that are everywhere parallel to the local slope.
a)  $ \dot{x} = x $ 
b)  $ \dot{x} = 1 - x^2 $ 
c)  $ \dot{x} = 1 - 4x(1-x) $ 
d)  $ \dot{x} = \sin(x) $ 

**2.8.3 (Calibrating the Euler method)** 
The goal of this problem is to test the Euler method on the initial value problem  $ \dot{x} = -x, \, x(0) = 1 $ .
a) Solve the problem analytically. What is the exact value of  $ x(1) $ ?
b) Using the Euler method with step size  $ \Delta t = 1 $ , estimate  $ x(1) $  numerically—call the result  $ \hat{x}(1) $ . Then repeat, using  $ \Delta t = 10^{-n} $ , for  $ n = 1, 2, 3, 4 $ .
c) Plot the error  $ E = |x(1) - \hat{x}(1)| $  as a function of  $ \Delta t $ . Then plot  $ \ln(E) $  vs.  $ \ln(\Delta t) $ . Explain the results.

**2.8.4** Redo Exercise 2.8.3, using the improved Euler method.
**2.8.5** Redo Exercise 2.8.3, using the Runge-Kutta method.

**2.8.6 (Analytically intractable problem)** 
Consider the initial value problem  $ \dot{x} = x + e^{-x}, \, x(0) = 0 $ . This problem can’t be solved analytically.
a) Sketch the solution  $ x(t) $  for  $ t \ge 0 $ .
b) Using analytical arguments, obtain rigorous bounds on the value of  $ x $  at  $ t=1 $ . Prove that  $ a \le x(1) \le b $ . Try to make  $ a $  and  $ b $  as close as possible. *(Hint: Bound the given vector field by approximate vector fields that can be integrated analytically.)*
c) Using the Euler method, compute  $ x $  at  $ t=1 $ , correct to three decimal places. How small does the step size need to be to obtain the desired accuracy?
d) Repeat part (c), now using the Runge-Kutta method. Compare the results for step sizes  $ \Delta t = 1, \, 0.1, \, 0.01 $ .

**2.8.7 (Error estimate for Euler method)** 
Use Taylor series expansions to estimate the error in taking one step by the Euler method. Compare the exact value  $ x(t_0 + \Delta t) $  with the Euler approximation  $ x_0 + f(x_0)\Delta t $ . Show that the local error is  $ O(\Delta t^2) $ .

**2.8.8 (Error estimate for the improved Euler method)** 
Use Taylor series arguments to show that the local error for the improved Euler method is  $ O(\Delta t^3) $ .

**2.8.9 (Error estimate for Runge-Kutta)** 
Show that the Runge-Kutta method produces a local error of size  $ O(\Delta t^5) $ . *(Warning: This calculation involves massive amounts of algebra. Teach yourself a symbolic manipulation language and do the problem on the computer.)*


