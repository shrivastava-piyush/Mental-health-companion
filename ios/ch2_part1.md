# Chapter 2: Flows on the Line

## 2.0 Introduction

In Chapter 1, we introduced the general system of equations for dynamical systems:
$$ \dot{x}_1 = f_1(x_1, \dots, x_n) $$
$$ \vdots $$
$$ \dot{x}_n = f_n(x_1, \dots, x_n) $$

We mentioned that the solutions to these equations can be visualized as trajectories flowing through an $n$-dimensional phase space. At this point, that concept probably sounds like a mind-bending abstraction. So, let’s start slowly and ground ourselves by looking at the simplest case: $n=1$. 

In an $n=1$ system, we only have a single equation, which takes the form:
$$ \dot{x} = f(x) $$

Here, $x(t)$ is a real-valued function representing the state of the system over time $t$, and $f(x)$ is a smooth mathematical function of $x$. We call these equations **one-dimensional** or **first-order systems**.

Before moving forward, let's clarify two potential points of confusion regarding terminology:
1. **"System":** We are using the word "system" in the sense of a *dynamical system*, not in the classical algebra sense of a collection of two or more equations. Thus, a single differential equation can be a "system."
2. **Time Independence:** We do *not* allow $f$ to depend explicitly on time. Time-dependent (or "nonautonomous") equations like $\dot{x} = f(x, t)$ are more complicated because you need two pieces of information ($x$ and $t$) to predict the future. Therefore, $\dot{x} = f(x, t)$ is technically a two-dimensional (second-order) system and will be discussed later.

---

## 2.1 A Geometric Way of Thinking

When analyzing nonlinear systems, pictures are often far more helpful than complicated formulas. We will illustrate this with a simple example that introduces one of the most foundational techniques in dynamics: interpreting a differential equation as a **vector field**.

Consider the nonlinear differential equation:
$$ \dot{x} = \sin(x) $$

To highlight why pictures are better than formulas here, we purposefully chose one of the few nonlinear equations that *can* actually be solved exactly. Using separation of variables:
$$ \frac{dx}{\sin(x)} = dt $$
Integrating both sides gives:
$$ t = \int \csc(x) \,dx = -\ln|\csc(x) + \cot(x)| + C $$

If we assume the initial condition $x=x_0$ at $t=0$, we can solve for $C$ to get the exact solution:
$$ t = \ln \left| \frac{\csc(x_0) + \cot(x_0)}{\csc(x) + \cot(x)} \right| $$

This formula is exact, but it is a massive headache to interpret! For example, if $x_0 = \pi/4$, can you easily describe what happens as $t \to \infty$? What if $x_0$ is any arbitrary number? Staring at the formula won't give you an intuitive answer quickly.

In stark contrast, a graphical analysis of $\dot{x} = \sin(x)$ is clear and simple. 

We can think of $t$ as time, $x$ as the position of an imaginary particle moving along a line (the x-axis), and $\dot{x}$ as the velocity of that particle. The differential equation $\dot{x} = \sin(x)$ represents a **vector field** on the line: it dictates exactly how fast and in what direction the particle must move based on its current position $x$.

To sketch the vector field:
1. Plot $\dot{x}$ versus $x$ (which is just graphing the sine wave).
2. Draw arrows on the x-axis to indicate velocity. If $\dot{x} > 0$ (the sine wave is above the axis), the arrow points to the right. If $\dot{x} < 0$ (the wave is below the axis), the arrow points to the left.

### The Flow of Phase Fluid
A more physical way to imagine this is that a fluid is flowing steadily along the x-axis, and its velocity varies from place to place according to the rule $\dot{x} = \sin(x)$. 

At points where $\dot{x} = 0$, there is no flow. These are called **fixed points**. There are two kinds of fixed points:
- **Stable fixed points (attractors or sinks):** The flow on both sides points toward them. (Represented as solid black dots).
- **Unstable fixed points (repellers or sources):** The flow on both sides points away from them. (Represented as open circles).

Armed with this picture, solving the system visually is easy. We drop an imaginary particle at a starting position $x_0$ and watch how the fluid carries it:
- If we start at $x_0 = \pi/4$, the particle moves right because $\sin(\pi/4) > 0$. It moves faster and faster until it hits $x = \pi/2$ (where velocity is maximum). After that, it slows down and gradually approaches the stable fixed point at $x = \pi$, but it takes infinite time to get there.
- The same logic applies to any initial condition. A particle simply moves in the direction of the arrows until it asymptotes toward the nearest stable fixed point. If it starts exactly on an unstable fixed point, it stays there forever (but the slightest nudge will send it flying away toward a stable one).

While pictures can't give us exact quantitative numbers (like the exact time maximum speed is reached), they immediately reveal the qualitative behavior, which is often exactly what we care about.

---

## 2.2 Fixed Points and Stability

The geometric ideas from the previous section can be applied to *any* one-dimensional system $\dot{x} = f(x)$. You simply draw the graph of $f(x)$ and use it to sketch the vector field on the real line.

This imaginary line is called the **phase space**, and the imaginary fluid flowing along it is the **phase fluid**. To find the solution $x(t)$ starting from $x_0$, we place an imaginary **phase point** at $x_0$ and watch it flow. The resulting function $x(t)$ is called the **trajectory**. A complete picture showing all the qualitatively different trajectories of a system is a **phase portrait**.

The behavior of the phase portrait is entirely controlled by the fixed points $x^*$, defined mathematically as $f(x^*) = 0$. 

In terms of differential equations, fixed points represent **equilibrium solutions** (steady or constant solutions). If the system starts at $x^*$, it stays there forever. 
- An equilibrium is **stable** if small disturbances away from it naturally dampen out and return to the equilibrium (stable fixed point).
- An equilibrium is **unstable** if small disturbances grow over time, pushing the system further away (unstable fixed point).

### Example 2.2.1
Find all fixed points for $\dot{x} = x^2 - 1$ and classify their stability.
**Solution:** Set $\dot{x} = 0$ to get $x^2 - 1 = 0$. The fixed points are $x^* = 1$ and $x^* = -1$.
Graphing $f(x) = x^2 - 1$ reveals a parabola opening upwards. For $x > 1$ and $x < -1$, the parabola is positive (flow is right). Between $-1 < x < 1$, it is negative (flow is left). Therefore, the flow pushes towards $-1$ (it is a stable fixed point) and pushes away from $1$ (it is an unstable fixed point). 
*Note on stability:* The point $-1$ is "locally stable" because small disturbances decay. However, if a massive disturbance pushes the point past $+1$, the system will fly off to infinity rather than returning to $-1$. It is not "globally stable."

### Example 2.2.2: An Electrical Circuit
Consider an RC circuit (a resistor $R$ and a capacitor $C$ in series with a constant battery voltage $V_0$). When the switch is closed, there is initially no charge on the capacitor. Let $Q(t)$ be the charge at time $t$. Sketch $Q(t)$.
**Solution:** Following Kirchhoff's voltage law around the loop, the voltage drop must be zero:
$-V_0 + IR + Q/C = 0$. 
Since the current $I$ is the rate of change of charge ($I = \dot{Q}$), we get:
$V_0 = R\dot{Q} + Q/C$, which rearranges to $\dot{Q} = \frac{V_0}{R} - \frac{Q}{RC}$.

This is our function $f(Q)$. Graphing $\dot{Q}$ against $Q$ gives a straight line with a negative slope. The fixed point is where $\dot{Q} = 0$, which is $Q^* = CV_0$. Because the slope is negative, the flow is to the right when $Q < CV_0$ and to the left when $Q > CV_0$. Thus, $Q^* = CV_0$ is a globally stable fixed point. If we start at $Q=0$, the system flows smoothly and monotonically towards $CV_0$, slowing down as it approaches. 

### Example 2.2.3
Sketch the phase portrait for $\dot{x} = x - \cos(x)$ and determine stability.
**Solution:** We could graph $x - \cos(x)$ directly, but that’s tricky. Instead, note that fixed points occur where $x - \cos(x) = 0$, or where $x = \cos(x)$. 
If we graph $y = x$ and $y = \cos(x)$ on the same axes, their intersection is the fixed point $x^*$. When the line $y=x$ is above the cosine curve, $\dot{x} > 0$ (flow right). When it is below, $\dot{x} < 0$ (flow left). The intersection shows the flow pointing away in both directions. Therefore, there is exactly one fixed point, and it is strictly unstable—and we figured this out without ever needing a formula for $x^*$!

---

## 2.3 Population Growth

The simplest mathematical model for a growing population is:
$$ \dot{N} = rN $$
where $N(t)$ is the population at time $t$, and $r > 0$ is the growth rate. This predicts basic, unconstrained exponential growth: $N(t) = N_0 e^{rt}$.

In reality, exponential growth cannot continue forever. Overcrowding and limited resources eventually take their toll. To account for this, biologists assume that the *per capita growth rate* ($\dot{N}/N$) decreases when the population gets sufficiently large. 

For small $N$, the growth rate is roughly $r$. But for populations larger than a certain environmental **carrying capacity** $K$, the growth rate becomes negative (the death rate exceeds the birth rate).

A mathematically elegant way to model this is to assume the per capita growth rate decreases linearly as $N$ increases. This gives us the famous **logistic equation** (first proposed by Verhulst in 1838 for human populations):
$$ \dot{N} = rN \left(1 - \frac{N}{K}\right) $$

While we could solve this analytically, the geometric approach is much more intuitive. We plot $\dot{N}$ versus $N$ (only for $N \ge 0$, since negative population doesn't make sense). This results in a downward-opening parabola passing through the origin.

Setting $\dot{N} = 0$ gives two fixed points: $N^* = 0$ and $N^* = K$.
Looking at the flow on the parabola:
- Between $0$ and $K$, $\dot{N}$ is positive, so the population grows.
- Above $K$, $\dot{N}$ is negative, so the population shrinks.

This means $N^* = 0$ is an unstable fixed point (a tiny population will explode exponentially), and $N^* = K$ is a stable fixed point (the population always settles at the carrying capacity). 

If we start with $N_0 < K/2$, the population grows increasingly fast (accelerating) until it hits the peak of the parabola at $K/2$. After that, growth slows down (decelerating) until it asymptotes at $K$. This creates a classic S-shaped (sigmoid) growth curve. If we start with $N_0 > K$, the population simply decays smoothly down to $K$.

### Critique of the Logistic Model
We shouldn't take the algebraic form of the logistic model too literally; it is essentially a mathematical metaphor for populations that grow from zero to a carrying capacity. It works beautifully for simple laboratory colonies of yeast or bacteria grown in constant conditions. However, it fails for complex organisms (like fruit flies or beetles) that have complex life cycles (eggs, larvae, adults) or experience time-delayed effects from overcrowding, which often lead to persistent fluctuations rather than a smooth approach to a steady limit.

---

## 2.4 Linear Stability Analysis

While graphical methods are fantastic for determining stability, we often want a quantitative measure, such as exactly *how fast* a system decays to a stable fixed point. We can find this using **linear stability analysis**, which involves linearizing the system around the fixed point.

Let $x^*$ be a fixed point, and let $\eta(t) = x(t) - x^*$ represent a very small perturbation (or disturbance) away from the fixed point. We want to know if $\eta(t)$ grows or shrinks.

Taking the derivative with respect to time:
$$ \dot{\eta} = \dot{x} $$
(Since $x^*$ is a constant). We know $\dot{x} = f(x)$, so:
$$ \dot{\eta} = f(x^* + \eta) $$

Because $\eta$ is very small, we can use a Taylor series expansion:
$$ f(x^* + \eta) = f(x^*) + \eta f'(x^*) + O(\eta^2) $$
Where $O(\eta^2)$ represents quadratically small, negligible terms. Because $x^*$ is a fixed point, $f(x^*) = 0$. This leaves us with:
$$ \dot{\eta} \approx \eta f'(x^*) $$

This is a linear differential equation for the perturbation $\eta$. The term $f'(x^*)$ is just a constant (it is the slope of the $f(x)$ graph at the fixed point). 

This equation shows that the perturbation grows exponentially if $f'(x^*) > 0$ and decays exponentially if $f'(x*) < 0$. If $f'(x^*) = 0$, the $O(\eta^2)$ terms are no longer negligible, and this linear analysis fails—we must return to graphical methods.

**The Takeaway:** The slope $f'(x^*)$ at the fixed point determines stability. Its sign tells us if it is stable or unstable, and its magnitude $|f'(x^*)|$ acts as the exponential decay/growth rate. The reciprocal $1/|f'(x^*)|$ is the **characteristic time scale**, representing the time required for the system to undergo significant change near the fixed point.

### Example 2.4.1
Classify the stability of the fixed points for $\dot{x} = \sin(x)$ using linear stability analysis.
**Solution:** The fixed points are $x^* = k\pi$, where $k$ is an integer. The derivative is $f'(x) = \cos(x)$. 
Evaluating the derivative at the fixed points: $f'(k\pi) = \cos(k\pi)$. 
This equals $1$ if $k$ is even, and $-1$ if $k$ is odd. Therefore, the fixed points are unstable for even multiples of $\pi$ and stable for odd multiples, which perfectly matches our graphical analysis.

### Example 2.4.3: When Linear Analysis Fails
What happens if $f'(x^*) = 0$? 
**Solution:** The linear approximation fails, and anything can happen. We must check graphically. For example, consider:
- (a) $\dot{x} = -x^3$ (Stable at $x=0$)
- (b) $\dot{x} = x^3$ (Unstable at $x=0$)
- (c) $\dot{x} = x^2$ (Half-stable at $x=0$: attracts from the left, repels to the right)
- (d) $\dot{x} = 0$ (A whole line of fixed points; neutrally stable)
All of these have $f'(0) = 0$, but their stabilities are completely different. Such cases are critically important when we study **bifurcations** later on.
