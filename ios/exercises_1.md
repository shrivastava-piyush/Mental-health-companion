# Exercises for Chapter 2

## 2.1 A Geometric Way of Thinking

**In the next three exercises, interpret $\dot{x} = \sin(x)$ as a flow on the line.**

**2.1.1** Find all the fixed points of the flow.

**2.1.2** At which points $x$ does the flow have the greatest velocity to the right?

**2.1.3** 
a) Find the flow’s acceleration $\ddot{x}$ as a function of $x$.
b) Find the points where the flow has maximum positive acceleration.

**2.1.4 (Exact solution of $\dot{x} = \sin(x)$)** 
As shown in the text, $\dot{x} = \sin(x)$ has the solution $t = \ln \left| \frac{\csc(x_0) + \cot(x_0)}{\csc(x) + \cot(x)} \right|$, where $x_0 = x(0)$ is the initial value of $x$.
a) Given the specific initial condition $x_0 = \pi/4$, show that the solution above can be inverted to obtain $x(t) = 2 \arctan\left(\frac{e^t}{1 + \sqrt{2}}\right)$. Conclude that $x(t) \to \pi$ as $t \to \infty$, as claimed in Section 2.1. *(Hint: You need to be good with trigonometric identities to solve this problem.)*
b) Try to find the analytical solution for $x(t)$, given an arbitrary initial condition $x_0$.

**2.1.5 (A mechanical analog)**
a) Find a mechanical system that is approximately governed by $\dot{x} = \sin(x)$.
b) Using your physical intuition, explain why it now becomes obvious that $x^* = 0$ is an unstable fixed point and $x^* = \pi$ is stable.

---

## 2.2 Fixed Points and Stability

**Analyze the following equations graphically. In each case, sketch the vector field on the real line, find all the fixed points, classify their stability, and sketch the graph of $x(t)$ for different initial conditions. Then try for a few minutes to obtain the analytical solution for $x(t)$; if you get stuck, don’t try for too long since in several cases it’s impossible to solve the equation in closed form!**

**2.2.1** $\dot{x} = 4x^2 - 16$
**2.2.2** $\dot{x} = 1 - x^{14}$
**2.2.3** $\dot{x} = x - x^3$
**2.2.4** $\dot{x} = e^{-x} \sin(x)$
**2.2.5** $\dot{x} = 1 + \frac{1}{2} \cos(x)$
**2.2.6** $\dot{x} = 1 - 2\cos(x)$
**2.2.7** $\dot{x} = e^x - \cos(x)$ *(Hint: Sketch the graphs of $e^x$ and $\cos(x)$ on the same axes, and look for intersections. You won’t be able to find the fixed points explicitly, but you can still find the qualitative behavior.)*

**2.2.8 (Working backwards, from flows to equations)** 
Given an equation $\dot{x} = f(x)$, we know how to sketch the corresponding flow on the real line. Here you are asked to solve the opposite problem: For a phase portrait that has unstable fixed points at $x=-1$ and $x=2$, and a stable fixed point at $x=0$, find an equation that is consistent with it. *(There are an infinite number of correct answers—and wrong ones too.)*

**2.2.9 (Backwards again, now from solutions to equations)** 
Find an equation $\dot{x} = f(x)$ whose solutions $x(t)$ monotonically approach $x=1$ from above, and monotonically approach $x=-1$ from below, with an unstable fixed point at $x=0$.

**2.2.10 (Fixed points)** 
For each of (a)–(e), find an equation $\dot{x} = f(x)$ with the stated properties, or if there are no examples, explain why not. (In all cases, assume that $f(x)$ is a smooth function.)
a) Every real number is a fixed point.
b) Every integer is a fixed point, and there are no others.
c) There are precisely three fixed points, and all of them are stable.
d) There are no fixed points.
e) There are precisely 100 fixed points.

**2.2.11 (Analytical solution for charging capacitor)** 
Obtain the analytical solution of the initial value problem $\dot{Q} = \frac{V_0}{R} - \frac{Q}{RC}$, with $Q(0) = 0$, which arose in Example 2.2.2.

**2.2.12 (A nonlinear resistor)** 
Suppose the resistor in Example 2.2.2 is replaced by a nonlinear resistor. In other words, this resistor does not have a linear relation between voltage and current. Such nonlinearity arises in certain solid-state devices. Instead of $IR = V_0 - Q/C$, suppose we have $g(I) = V_0 - Q/C$, where $g(I)$ is an S-shaped nonlinear function. Redo Example 2.2.2 in this case. Derive the circuit equations, find all the fixed points, and analyze their stability. What qualitative effects does the nonlinearity introduce (if any)?

**2.2.13 (Terminal velocity)** 
The velocity $v(t)$ of a skydiver falling to the ground is governed by $m\dot{v} = mg - kv^2$, where $m$ is the mass of the skydiver, $g$ is the acceleration due to gravity, and $k > 0$ is a constant related to the amount of air resistance.
a) Obtain the analytical solution for $v(t)$, assuming that $v(0) = 0$.
b) Find the limit of $v(t)$ as $t \to \infty$. This limiting velocity is called the **terminal velocity**.
c) Give a graphical analysis of this problem, and thereby re-derive a formula for the terminal velocity.
d) An experimental study (Carlson et al. 1942) confirmed that the equation gives a good quantitative fit to data on human skydivers. Six men were dropped from altitudes varying from 10,600 feet to 31,400 feet to a terminal altitude of 2,100 feet, at which they opened their parachutes. The long free fall from 31,400 to 2,100 feet took 116 seconds. The average weight of the men and their equipment was 261.2 pounds. In these units, $g = 32.2 \, \text{ft/sec}^2$. Compute the average velocity $V_{avg}$.
e) Using the data given here, estimate the terminal velocity, and the value of the drag constant $k$. *(Hints: First you need to find an exact formula for $s(t)$, the distance fallen, where $s(0) = 0$, $\dot{s} = v$, and $v(t)$ is known from part (a). Then solve for $V$ graphically or numerically.)*

---

## 2.3 Population Growth

**2.3.1 (Exact solution of logistic equation)** 
There are two ways to solve the logistic equation $\dot{N} = rN\left(1 - \frac{N}{K}\right)$ analytically for an arbitrary initial condition $N_0$.
a) Separate variables and integrate, using partial fractions.
b) Make the change of variables $x = 1/N$. Then derive and solve the resulting differential equation for $x$.

**2.3.2 (Autocatalysis)** 
Consider the model chemical reaction $A + X \rightleftharpoons 2X$ with forward rate $k_1$ and backward rate $k_{-1}$. This means that the chemical $X$ stimulates its own production, a process called **autocatalysis**. Assuming there's an enormous surplus of chemical $A$, so its concentration $a$ is constant, the equation for the kinetics of $x$ (concentration of $X$) is:
$$ \dot{x} = k_1 a x - k_{-1} x^2 $$
a) Find all the fixed points of this equation and classify their stability.
b) Sketch the graph of $x(t)$ for various initial values $x_0$.

**2.3.3 (Tumor growth)** 
The growth of cancerous tumors can be modeled by the Gompertz law $\dot{N} = -aN \ln(bN)$, where $N(t)$ is proportional to the number of cells in the tumor, and $a, b > 0$ are parameters.
a) Interpret $a$ and $b$ biologically.
b) Sketch the vector field and then graph $N(t)$ for various initial values.

**2.3.4 (The Allee effect)** 
For certain species, the effective growth rate $\dot{N}/N$ is highest at intermediate $N$. This is called the **Allee effect**. For example, it is too hard to find mates when $N$ is very small, and there is too much competition when $N$ is large.
a) Show that $\dot{N}/N = r - a(N - b)^2$ provides an example of the Allee effect, if $r, a,$ and $b$ satisfy certain constraints (to be determined).
b) Find all the fixed points of the system and classify their stability.
c) Sketch the solutions $N(t)$ for different initial conditions.
d) Compare the solutions $N(t)$ to those found for the logistic equation. What are the qualitative differences, if any?

**2.3.5 (Dominance of the fittest)** 
Suppose $X$ and $Y$ are two species that reproduce exponentially fast: $\dot{X} = aX$ and $\dot{Y} = bY$, respectively, with $X(0) > 0, Y(0) > 0$, and growth rates $a > b > 0$. Here $X$ is "fitter" than $Y$. We expect $X$ to keep increasing its share of the total population $X+Y$ as $t \to \infty$.
a) Let $x(t) = X(t) / [X(t) + Y(t)]$ denote $X$'s share of the population. By solving for $X(t)$ and $Y(t)$, show that $x(t)$ increases monotonically and approaches $1$ as $t \to \infty$.
b) Alternatively, derive a differential equation for $x(t)$. Show that $x(t)$ obeys the logistic equation $\dot{x} = (a-b)x(1-x)$. Explain why this implies $x(t)$ approaches $1$.

**2.3.6 (Language death)** 
Abrams and Strogatz (2003) proposed a model of language competition. Let $x$ be the proportion of the population speaking language $X$, and $1-x$ the proportion speaking $Y$. The system evolves according to:
$$ \dot{x} = (1-x)x^a s - x(1-x)^a (1-s) $$
where $0 < s < 1$ reflects the social status of language $X$, and $a > 1$ is an adjustable parameter.
a) Show that this equation for $x$ has three fixed points.
b) Show that for all $a > 1$, the fixed points at $x=0$ and $x=1$ are both stable.
c) Show that the third fixed point, $0 < x^* < 1$, is unstable.
*(This model therefore predicts that two languages cannot coexist stably—one will eventually drive the other to extinction.)*